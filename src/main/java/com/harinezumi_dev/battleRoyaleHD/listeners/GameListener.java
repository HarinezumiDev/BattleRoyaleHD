package com.harinezumi_dev.battleRoyaleHD.listeners;

import com.harinezumi_dev.battleRoyaleHD.game.GameManager;
import com.harinezumi_dev.battleRoyaleHD.game.GamePhase;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {
    private final GameManager gameManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getFrozenPlayers().containsKey(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        GamePhase phase = gameManager.getCurrentPhase();

        if (phase == GamePhase.MINING || phase == GamePhase.WAITING || phase == GamePhase.COUNTDOWN) {
            event.setCancelled(true);
        } else if (phase == GamePhase.FIGHT || phase == GamePhase.OVERTIME) {
            Player attacker = (Player) event.getDamager();
            gameManager.getQuickDeathManager().recordDamage(attacker, event.getFinalDamage());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        GamePhase phase = gameManager.getCurrentPhase();

        if (phase == GamePhase.FIGHT || phase == GamePhase.OVERTIME) {
            gameManager.removePlayer(player);
            event.setDeathMessage("§c" + player.getName() + " was eliminated!");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePhase phase = gameManager.getCurrentPhase();

        if (phase == GamePhase.MINING) {
            event.setRespawnLocation(gameManager.getSettings().getSpawnLocation());
        } else if (phase == GamePhase.FIGHT || phase == GamePhase.OVERTIME) {
            player.setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(gameManager.getSettings().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        gameManager.addPlayerToBossBar(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        gameManager.removePlayerFromBossBar(player);

        GamePhase phase = gameManager.getCurrentPhase();
        if (phase == GamePhase.FIGHT || phase == GamePhase.OVERTIME) {
            gameManager.removePlayer(player);
        }
    }
}
