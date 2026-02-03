package com.harinezumi_dev.battleRoyaleHD;

import com.harinezumi_dev.battleRoyaleHD.commands.BRCommand;
import com.harinezumi_dev.battleRoyaleHD.commands.BRTabCompleter;
import com.harinezumi_dev.battleRoyaleHD.config.ConfigManager;
import com.harinezumi_dev.battleRoyaleHD.game.GameManager;
import com.harinezumi_dev.battleRoyaleHD.game.GameSettings;
import com.harinezumi_dev.battleRoyaleHD.listeners.GameListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattleRoyaleHD extends JavaPlugin {
    private ConfigManager configManager;
    private GameManager gameManager;
    private BRCommand brCommand;
    private GameListener gameListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        GameSettings settings = configManager.loadSettings();

        gameManager = new GameManager(this, settings);

        brCommand = new BRCommand(this, gameManager, configManager);
        gameListener = new GameListener(gameManager);

        getCommand("br").setExecutor(brCommand);
        getCommand("br").setTabCompleter(new BRTabCompleter());

        getServer().getPluginManager().registerEvents(gameListener, this);

        getLogger().info("BattleRoyaleHD enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.resetGame();
        }
        getLogger().info("BattleRoyaleHD disabled!");
    }
}
