package com.harinezumi_dev.battleRoyaleHD.config;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import com.harinezumi_dev.battleRoyaleHD.game.GameSettings;
import com.harinezumi_dev.battleRoyaleHD.game.QuickDeathType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final BattleRoyaleHD plugin;

    public ConfigManager(BattleRoyaleHD plugin) {
        this.plugin = plugin;
    }

    public void loadDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    public GameSettings loadSettings() {
        FileConfiguration config = plugin.getConfig();

        int miningBorderDiameter = config.getInt("mining_border_diameter", 1000);
        int miningPhaseTime = config.getInt("mining_phase_time", 10);
        int fightBorderDiameter = config.getInt("fight_border_diameter", 100);
        int fightPhaseTime = config.getInt("fight_phase_time", 15);
        int overtimeBorderDiameter = config.getInt("overtime_border_diameter", 20);
        int overtimePhaseTime = config.getInt("overtime_phase_time", 5);

        double spawnX = config.getDouble("spawn_x", 0.0);
        double spawnY = config.getDouble("spawn_y", 100.0);
        double spawnZ = config.getDouble("spawn_z", 0.0);
        String worldName = config.getString("spawn_world", "world");

        boolean bossbarEnable = config.getBoolean("bossbar_enable", true);
        BarColor phaseColorMining = parseBarColor(config.getString("phase_color_mining", "BLUE"));
        BarColor phaseColorFight = parseBarColor(config.getString("phase_color_fight", "RED"));
        BarColor phaseColorOvertime = parseBarColor(config.getString("phase_color_overtime", "PURPLE"));
        int invisibilityTime = config.getInt("invisibility_time", 3);
        QuickDeathType quickDeathType = parseQuickDeathType(config.getString("quick_death", "random"));

        Location spawnLocation = new Location(
            Bukkit.getWorld(worldName),
            spawnX, spawnY, spawnZ
        );

        return new GameSettings(
            miningBorderDiameter, miningPhaseTime,
            fightBorderDiameter, fightPhaseTime,
            overtimeBorderDiameter, overtimePhaseTime,
            spawnLocation, bossbarEnable,
            phaseColorMining, phaseColorFight, phaseColorOvertime,
            invisibilityTime, quickDeathType
        );
    }

    public void saveSpawnLocation(Location location) {
        FileConfiguration config = plugin.getConfig();

        if (location != null && location.getWorld() != null) {
            config.set("spawn_x", location.getX());
            config.set("spawn_y", location.getY());
            config.set("spawn_z", location.getZ());
            config.set("spawn_world", location.getWorld().getName());
        }

        plugin.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }

    private BarColor parseBarColor(String color) {
        try {
            return BarColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarColor.WHITE;
        }
    }

    private QuickDeathType parseQuickDeathType(String type) {
        try {
            return QuickDeathType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QuickDeathType.RANDOM;
        }
    }
}
