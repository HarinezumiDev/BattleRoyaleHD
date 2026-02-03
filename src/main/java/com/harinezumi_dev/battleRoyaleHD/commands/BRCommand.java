package com.harinezumi_dev.battleRoyaleHD.commands;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import com.harinezumi_dev.battleRoyaleHD.config.ConfigManager;
import com.harinezumi_dev.battleRoyaleHD.game.GameManager;
import com.harinezumi_dev.battleRoyaleHD.game.GamePhase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BRCommand implements CommandExecutor {
    private final BattleRoyaleHD plugin;
    private final GameManager gameManager;
    private final ConfigManager configManager;

    public BRCommand(BattleRoyaleHD plugin, GameManager gameManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "setspawn":
                return handleSetSpawn(sender);
            case "start":
                return handleStart(sender, args);
            case "stop":
                return handleStop(sender);
            case "reload":
                return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Battle Royale Commands ===");
        sender.sendMessage("§e/br setspawn §7- Set spawn location");
        sender.sendMessage("§e/br start <seconds> §7- Start game with countdown");
        sender.sendMessage("§e/br stop §7- Stop current game");
        sender.sendMessage("§e/br reload §7- Reload configuration");
    }

    private boolean handleSetSpawn(CommandSender sender) {
        if (!sender.hasPermission("battleroyale.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        configManager.saveSpawnLocation(player.getLocation());
        gameManager.getSettings().setSpawnLocation(player.getLocation());

        sender.sendMessage("§aSpawn location set!");
        return true;
    }

    private boolean handleStart(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleroyale.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /br start <countdown_seconds>");
            return true;
        }

        if (gameManager.getCurrentPhase() != GamePhase.WAITING) {
            sender.sendMessage("§cGame is already in progress!");
            return true;
        }

        try {
            int countdown = Integer.parseInt(args[1]);

            if (countdown < 0) {
                sender.sendMessage("§cCountdown must be positive!");
                return true;
            }

            gameManager.startGame(countdown);
            sender.sendMessage("§aGame starting in " + countdown + " seconds!");
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cCountdown must be a number!");
            return true;
        }
    }

    private boolean handleStop(CommandSender sender) {
        if (!sender.hasPermission("battleroyale.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (gameManager.getCurrentPhase() == GamePhase.WAITING) {
            sender.sendMessage("§cNo game is running!");
            return true;
        }

        gameManager.stopGame();
        sender.sendMessage("§aGame stopped!");
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("battleroyale.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        configManager.reloadConfig();
        gameManager.updateSettings(configManager.loadSettings());

        sender.sendMessage("§aConfiguration reloaded!");
        return true;
    }
}
