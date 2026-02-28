package com.harinezumi_dev.battleRoyaleHD;

import com.harinezumi_dev.battleRoyaleHD.commands.BRCommand;
import com.harinezumi_dev.battleRoyaleHD.commands.BRTabCompleter;
import com.harinezumi_dev.battleRoyaleHD.commands.KitCommand;
import com.harinezumi_dev.battleRoyaleHD.config.ConfigManager;
import com.harinezumi_dev.battleRoyaleHD.game.GameManager;
import com.harinezumi_dev.battleRoyaleHD.game.GameSettings;
import com.harinezumi_dev.battleRoyaleHD.listeners.GameListener;
import com.harinezumi_dev.battleRoyaleHD.utils.KitManager;
import com.harinezumi_dev.battleRoyaleHD.utils.PersistenceService;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattleRoyaleHD extends JavaPlugin {
    private ConfigManager configManager;
    private GameManager gameManager;
    private PersistenceService persistenceService;
    private KitManager kitManager;
    private BRCommand brCommand;
    private KitCommand kitCommand;
    private GameListener gameListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        GameSettings settings = configManager.loadSettings();

        persistenceService = new PersistenceService(this);
        kitManager = new KitManager(this, persistenceService);
        kitManager.loadKits();

        gameManager = new GameManager(this, settings, kitManager);

        brCommand = new BRCommand(this, gameManager, configManager);
        kitCommand = new KitCommand(kitManager);
        gameListener = new GameListener(gameManager);

        getCommand("br").setExecutor(brCommand);
        getCommand("br").setTabCompleter(new BRTabCompleter());
        getCommand("kit").setExecutor(kitCommand);
        getCommand("kit").setTabCompleter(kitCommand);

        getServer().getPluginManager().registerEvents(gameListener, this);

        getLogger().info("BattleRoyaleHD enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.resetGame();
        }
        if (persistenceService != null) {
            persistenceService.close();
        }
        getLogger().info("BattleRoyaleHD disabled!");
    }
}