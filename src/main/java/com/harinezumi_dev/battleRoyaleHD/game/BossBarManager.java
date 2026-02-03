package com.harinezumi_dev.battleRoyaleHD.game;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
    private BossBar bossBar;
    private final GameSettings settings;

    public BossBarManager(GameSettings settings) {
        this.settings = settings;
    }

    public void createBossBar(String title, BarColor color) {
        if (!settings.isBossbarEnable()) {
            return;
        }

        if (bossBar != null) {
            bossBar.removeAll();
        }

        bossBar = Bukkit.createBossBar(title, color, BarStyle.SOLID);
        bossBar.setVisible(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
    }

    public void updateBossBar(String title, double progress) {
        if (!settings.isBossbarEnable() || bossBar == null) {
            return;
        }

        bossBar.setTitle(title);
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
    }

    public void updateColor(BarColor color) {
        if (!settings.isBossbarEnable() || bossBar == null) {
            return;
        }

        bossBar.setColor(color);
    }

    public void addPlayer(Player player) {
        if (!settings.isBossbarEnable() || bossBar == null) {
            return;
        }

        bossBar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public void removeBossBar() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }
}
