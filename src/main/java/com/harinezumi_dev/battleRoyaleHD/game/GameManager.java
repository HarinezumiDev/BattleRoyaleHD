package com.harinezumi_dev.battleRoyaleHD.game;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameManager {
    private final BattleRoyaleHD plugin;
    private GameSettings settings;
    private GamePhase currentPhase;
    private Set<Player> alivePlayers;
    private BossBarManager bossBarManager;
    private BukkitTask phaseTask;
    private BukkitTask bossBarTask;
    private long phaseStartTime;
    private long phaseDuration;
    private Map<Player, Location> frozenPlayers;

    public GameManager(BattleRoyaleHD plugin, GameSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.currentPhase = GamePhase.WAITING;
        this.alivePlayers = new HashSet<>();
        this.bossBarManager = new BossBarManager(settings);
        this.frozenPlayers = new HashMap<>();
    }

    public void updateSettings(GameSettings settings) {
        this.settings = settings;
        this.bossBarManager = new BossBarManager(settings);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public Set<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public Map<Player, Location> getFrozenPlayers() {
        return frozenPlayers;
    }

    public void startGame(int countdown) {
        if (currentPhase != GamePhase.WAITING) {
            return;
        }

        Location spawn = settings.getSpawnLocation();
        if (spawn == null || spawn.getWorld() == null) {
            return;
        }

        currentPhase = GamePhase.COUNTDOWN;
        alivePlayers.clear();
        frozenPlayers.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            alivePlayers.add(player);
            player.teleport(spawn);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setSaturation(20);
            frozenPlayers.put(player, spawn);
        }

        if (countdown > 0) {
            new BukkitRunnable() {
                int timeLeft = countdown;

                @Override
                public void run() {
                    if (timeLeft <= 0) {
                        startMiningPhase();
                        cancel();
                        return;
                    }

                    if (timeLeft <= 3) {
                        String title = timeLeft == 3 ? "§e3" : timeLeft == 2 ? "§63" : "§c1";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendTitle(title, "", 0, 20, 5);
                        }
                    } else {
                        Bukkit.broadcastMessage("§eBattle Royale starts in " + timeLeft + " seconds!");
                    }

                    timeLeft--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            startMiningPhase();
        }
    }

    private void startMiningPhase() {
        currentPhase = GamePhase.MINING;
        Location spawn = settings.getSpawnLocation();
        World world = spawn.getWorld();

        WorldBorder border = world.getWorldBorder();
        border.setCenter(spawn);
        border.setSize(settings.getMiningBorderDiameter());

        frozenPlayers.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§aGO!", "", 0, 20, 10);
            player.setGameMode(GameMode.SURVIVAL);

            if (settings.getInvisibilityTime() > 0) {
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    settings.getInvisibilityTime() * 20,
                    0,
                    false,
                    false,
                    false
                ));
            }
        }

        Bukkit.broadcastMessage("§a=== Mining Phase Started ===");
        Bukkit.broadcastMessage("§ePvP is disabled. Gather resources!");

        phaseStartTime = System.currentTimeMillis();
        phaseDuration = settings.getMiningPhaseTime() * 60L * 1000L;

        bossBarManager.createBossBar("§aMining Phase", settings.getPhaseColorMining());
        startBossBarUpdater();

        phaseTask = new BukkitRunnable() {
            @Override
            public void run() {
                startFightPhase();
            }
        }.runTaskLater(plugin, settings.getMiningPhaseTime() * 60L * 20L);
    }

    private void startFightPhase() {
        currentPhase = GamePhase.FIGHT;
        Location spawn = settings.getSpawnLocation();
        World world = spawn.getWorld();

        WorldBorder border = world.getWorldBorder();
        border.setSize(settings.getFightBorderDiameter(), settings.getFightPhaseTime() * 60L);

        Bukkit.broadcastMessage("§c=== Fight Phase Started ===");
        Bukkit.broadcastMessage("§cPvP is now enabled! Border is shrinking!");
        Bukkit.broadcastMessage("§cDeath is permanent!");

        phaseStartTime = System.currentTimeMillis();
        phaseDuration = settings.getFightPhaseTime() * 60L * 1000L;

        bossBarManager.updateColor(settings.getPhaseColorFight());

        phaseTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (alivePlayers.size() > 1) {
                    startOvertimePhase();
                } else {
                    endGame();
                }
            }
        }.runTaskLater(plugin, settings.getFightPhaseTime() * 60L * 20L);
    }

    private void startOvertimePhase() {
        currentPhase = GamePhase.OVERTIME;
        Location spawn = settings.getSpawnLocation();
        World world = spawn.getWorld();

        WorldBorder border = world.getWorldBorder();
        border.setSize(settings.getOvertimeBorderDiameter(), settings.getOvertimePhaseTime() * 60L);

        Bukkit.broadcastMessage("§4=== OVERTIME ===");
        Bukkit.broadcastMessage("§4Border shrinking to critical size!");

        phaseStartTime = System.currentTimeMillis();
        phaseDuration = settings.getOvertimePhaseTime() * 60L * 1000L;

        bossBarManager.updateColor(settings.getPhaseColorOvertime());

        phaseTask = new BukkitRunnable() {
            @Override
            public void run() {
                endGame();
            }
        }.runTaskLater(plugin, settings.getOvertimePhaseTime() * 60L * 20L);
    }

    public void endGame() {
        currentPhase = GamePhase.FINISHED;

        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.iterator().next();
            Bukkit.broadcastMessage("§6=== GAME OVER ===");
            Bukkit.broadcastMessage("§aWinner: " + winner.getName());
        } else if (alivePlayers.size() > 1) {
            Bukkit.broadcastMessage("§6=== GAME OVER ===");
            Bukkit.broadcastMessage("§eMultiple survivors: " + alivePlayers.size());
        } else {
            Bukkit.broadcastMessage("§6=== GAME OVER ===");
            Bukkit.broadcastMessage("§cNo survivors!");
        }

        resetGame();
    }

    public void stopGame() {
        if (currentPhase == GamePhase.WAITING) {
            return;
        }

        Bukkit.broadcastMessage("§c=== GAME STOPPED ===");
        resetGame();
    }

    public void resetGame() {
        currentPhase = GamePhase.WAITING;
        alivePlayers.clear();
        frozenPlayers.clear();

        if (phaseTask != null && !phaseTask.isCancelled()) {
            phaseTask.cancel();
        }

        if (bossBarTask != null && !bossBarTask.isCancelled()) {
            bossBarTask.cancel();
        }

        bossBarManager.removeBossBar();
    }

    public void removePlayer(Player player) {
        alivePlayers.remove(player);

        if (currentPhase == GamePhase.FIGHT || currentPhase == GamePhase.OVERTIME) {
            if (alivePlayers.size() == 1) {
                endGame();
            } else if (alivePlayers.isEmpty()) {
                endGame();
            }
        }
    }

    public boolean isAlive(Player player) {
        return alivePlayers.contains(player);
    }

    private void startBossBarUpdater() {
        bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentPhase == GamePhase.WAITING || currentPhase == GamePhase.FINISHED) {
                    cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - phaseStartTime;
                long remaining = phaseDuration - elapsed;

                if (remaining < 0) {
                    remaining = 0;
                }

                int seconds = (int) (remaining / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                String phaseText = "";
                switch (currentPhase) {
                    case MINING:
                        phaseText = "§aMining Phase";
                        break;
                    case FIGHT:
                        phaseText = "§cFight Phase";
                        break;
                    case OVERTIME:
                        phaseText = "§4OVERTIME";
                        break;
                    default:
                        break;
                }

                String timeText = String.format("%s - §f%02d:%02d", phaseText, minutes, seconds);
                double progress = 1.0 - ((double) elapsed / phaseDuration);

                bossBarManager.updateBossBar(timeText, progress);
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void addPlayerToBossBar(Player player) {
        bossBarManager.addPlayer(player);
    }

    public void removePlayerFromBossBar(Player player) {
        bossBarManager.removePlayer(player);
    }
}
