package com.harinezumi_dev.battleRoyaleHD.game;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class QuickDeathManager {
    private final BattleRoyaleHD plugin;
    private final GameManager gameManager;
    private BukkitRunnable quickDeathTask;
    private Map<Player, Double> damageDealt;
    private Random random;

    public QuickDeathManager(BattleRoyaleHD plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.damageDealt = new HashMap<>();
        this.random = new Random();
    }

    public void recordDamage(Player attacker, double damage) {
        damageDealt.put(attacker, damageDealt.getOrDefault(attacker, 0.0) + damage);
    }

    public void resetDamage() {
        damageDealt.clear();
    }

    public void startQuickDeath(QuickDeathType type, Location center, int borderSize) {
        if (type == QuickDeathType.NONE) {
            Bukkit.broadcastMessage("§e§lNo quick death event! Fight to the death!");
            return;
        }

        if (type == QuickDeathType.RANDOM) {
            QuickDeathType[] types = {
                QuickDeathType.LAVA,
                QuickDeathType.ARROWS,
                QuickDeathType.DAMAGE_DEALT,
                QuickDeathType.BOX,
                QuickDeathType.HUNGER
            };
            type = types[random.nextInt(types.length)];
        }

        switch (type) {
            case LAVA:
                startLavaRising(center);
                break;
            case ARROWS:
                startArrowRain(center, borderSize);
                break;
            case DAMAGE_DEALT:
                endByDamageDealt();
                break;
            case BOX:
                createObsidianBox(center, borderSize);
                break;
            case HUNGER:
                applyHunger();
                break;
            default:
                break;
        }
    }

    private void startLavaRising(Location center) {
        Bukkit.broadcastMessage("§6§l⚠ QUICK DEATH: LAVA RISING ⚠");

        World world = center.getWorld();
        int minY = world.getMinHeight();

        BukkitRunnable task = new BukkitRunnable() {
            int currentY = minY;

            @Override
            public void run() {
                if (gameManager.getCurrentPhase() != GamePhase.OVERTIME) {
                    cancel();
                    return;
                }

                if (currentY > center.getY() + 100) {
                    cancel();
                    return;
                }

                int radius = 500;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location loc = new Location(world, center.getX() + x, currentY, center.getZ() + z);
                        if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType().name().contains("WATER")) {
                            loc.getBlock().setType(Material.LAVA);
                        }
                    }
                }

                currentY++;
            }
        };
        quickDeathTask = task;
        task.runTaskTimer(plugin, 0L, 20L);
    }

    private void startArrowRain(Location center, int borderSize) {
        Bukkit.broadcastMessage("§6§l⚠ QUICK DEATH: ARROW RAIN ⚠");

        World world = center.getWorld();
        int maxY = world.getMaxHeight() - 10;
        int halfSize = borderSize / 2;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameManager.getCurrentPhase() != GamePhase.OVERTIME) {
                    cancel();
                    return;
                }

                double randomX = center.getX() + (random.nextDouble() * borderSize) - halfSize;
                double randomZ = center.getZ() + (random.nextDouble() * borderSize) - halfSize;
                Location arrowLoc = new Location(world, randomX, maxY, randomZ);

                Arrow arrow = world.spawnArrow(arrowLoc, new org.bukkit.util.Vector(0, -1, 0), 3.0f, 0);
                arrow.setDamage(2.0);
                arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
            }
        };
        quickDeathTask = task;
        task.runTaskTimer(plugin, 0L, 1L);
    }

    private void endByDamageDealt() {
        Bukkit.broadcastMessage("§6§l⚠ QUICK DEATH: DAMAGE DEALT ⚠");

        Player winner = null;
        double maxDamage = 0;

        Set<Player> alivePlayers = gameManager.getAlivePlayers();
        for (Player player : alivePlayers) {
            double damage = damageDealt.getOrDefault(player, 0.0);
            if (damage > maxDamage) {
                maxDamage = damage;
                winner = player;
            }
        }

        if (winner != null) {
            Player finalWinner = winner;
            Bukkit.broadcastMessage("§aWinner by damage dealt: " + finalWinner.getName() + " (§c" + String.format("%.1f", maxDamage) + " damage§a)");

            for (Player player : alivePlayers) {
                if (!player.equals(finalWinner)) {
                    player.setHealth(0);
                }
            }
        }
    }

    private void createObsidianBox(Location center, int borderSize) {
        Bukkit.broadcastMessage("§6§l⚠ QUICK DEATH: OBSIDIAN BOX ⚠");

        World world = center.getWorld();
        int halfSize = borderSize / 2;
        int boxHeight = 3;

        int minX = (int) center.getX() - halfSize;
        int maxX = (int) center.getX() + halfSize;
        int minZ = (int) center.getZ() - halfSize;
        int maxZ = (int) center.getZ() + halfSize;
        int minY = (int) center.getY();
        int maxY = (int) center.getY() + boxHeight;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    Location loc = new Location(world, x, y, z);

                    if (y == minY || y == maxY ||
                        x == minX || x == maxX ||
                        z == minZ || z == maxZ) {
                        loc.getBlock().setType(Material.OBSIDIAN);
                    } else {
                        if (loc.getBlock().getType() != Material.AIR) {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }

        for (Player player : gameManager.getAlivePlayers()) {
            player.teleport(new Location(world, center.getX(), center.getY() + 1, center.getZ()));
        }
    }

    private void applyHunger() {
        Bukkit.broadcastMessage("§6§l⚠ QUICK DEATH: ETERNAL HUNGER ⚠");

        for (Player player : gameManager.getAlivePlayers()) {
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.HUNGER,
                Integer.MAX_VALUE,
                10,
                false,
                false,
                false
            ));
        }
    }

    public void stop() {
        if (quickDeathTask != null && !quickDeathTask.isCancelled()) {
            quickDeathTask.cancel();
        }
    }
}
