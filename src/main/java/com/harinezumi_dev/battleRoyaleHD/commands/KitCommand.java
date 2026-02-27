package com.harinezumi_dev.battleRoyaleHD.commands;

import com.harinezumi_dev.battleRoyaleHD.models.Kit;
import com.harinezumi_dev.battleRoyaleHD.utils.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;
import java.util.stream.Collectors;

public class KitCommand implements TabExecutor {
    private final KitManager kitManager;

    public KitCommand(Kitmanager kitManager) {
        this.kitManager = kitManager;
        this.editSessions = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§6Kit Commands:");
            player.sendMessage("§e/kit create <name> §7- Create new kit");
            player.sendMessage("§e/kit edit <name> §7- Edit kit");
            player.sendMessage("§e/kit confirm §7- Confirm kit edit");
            player.sendMessage("§e/kit cancel §7- Cancel kit edit");
            player.sendMessage("§e/kit preview <name> §7- Preview kit inventory");
            player.sendMessage("§e/kit list §7- List all kits");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreate(player, args);

            case "edit":
                return handleEdit(player, args);

            case "confirm":
                return handleConfirm(player);

            case "cancel":
                return handleCancel(player);

            case "preview":
                return handlePreview(player, args);

            case "list":
                return handleList(player);

            default:
                player.sendMessage("§cUnknown subcommand");
                return false;
        }
    }

    private boolean handleCreate(Player player, String[] args) {
        if (!player.hasPermission("battleroyale.kit.create")) {
            player.sendMessage("§cNo permission");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /kit create <name>");
            return true;
        }

        String name = args[1];

        if (kitManager.publicKitExists(name)) {
            player.sendMessage("§cKit already exists: " + name);
            return true;
        }

        kitManager.createPublicKit(name, player);
        player.sendMessage("§aKit created: " + name);
        return true;
    }

    private boolean handleEdit(Player player, String[] args) {
        if (!player.hasPermission("battleroyale.kit.edit")) {
            player.sendMessage("§cNo permission");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /kit edit <name>");
            return true;
        }

        String name = args[1];
        Kit kit = kitManager.getKit(name);

        if (kit == null) {
            player.sendMessage("§cKit not found: " + name);
            return true;
        }

        if (editSessions.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou already have an active edit session");
            player.sendMessage("§eUse §f/kit confirm §eor §f/kit cancel");
            return true;
        }

        kit.applyTo(player);
        editSessions.put(player.getUniqueId(), new KitEditSession(name));

        player.sendMessage("§aEditing kit: " + name);
        player.sendMessage("§eModify your inventory and armor");
        player.sendMessage("§eUse §f/kit confirm §eto save changes");
        player.sendMessage("§eUse §f/kit cancel §eto discard changes");

        return true;
    }

    private boolean handleConfirm(Player player) {
        KitEditSession session = editSessions.remove(player.getUniqueId());

        if (session == null) {
            player.sendMessage("§cNo active edit session");
            return true;
        }

        kitManager.updatePublicKit(session.kitName, player);
        player.sendMessage("§aKit updated: " + session.kitName);

        return true;
    }

    private boolean handleCancel(Player player) {
        KitEditSession session = editSessions.remove(player.getUniqueId());

        if (session == null) {
            player.sendMessage("§cNo active edit session");
            return true;
        }

        player.sendMessage("§eEdit cancelled");
        return true;
    }

    private boolean handlePreview(Player player, String[] args) {
        if (!player.hasPermission("battleroyale.kit.preview")) {
            player.sendMessage("§cNo permission");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /kit preview <name>");
            return true;
        }

        String name = args[1];
        Kit kit = kitManager.getKit(name);

        if (kit == null) {
            player.sendMessage("§cKit not found: " + name);
            return true;
        }

        Inventory preview = Bukkit.createInventory(null, 54, "§6Kit Preview: §e" + name);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 54; i++) {
            preview.setItem(i, filler);
        }

        ItemStack[] armor = kit.getArmor();
        if (armor[3] != null) preview.setItem(0, armor[3]);
        if (armor[2] != null) preview.setItem(1, armor[2]);
        if (armor[1] != null) preview.setItem(2, armor[1]);
        if (armor[0] != null) preview.setItem(3, armor[0]);
        if (kit.getOffhand() != null) preview.setItem(4, kit.getOffhand());

        ItemStack[] contents = kit.getContents();
        for (int i = 9; i < 36; i++) {
            if (contents[i] != null) {
                preview.setItem(i, contents[i]);
            }
        }

        for (int i = 0; i < 9; i++) {
            if (contents[i] != null) {
                preview.setItem(45 + i, contents[i]);
            }
        }

        player.openInventory(preview);
        return true;
    }

    private boolean handleList(Player player) {
        Collection<Kit> kits = kitManager.getPublicKits();

        if (kits.isEmpty()) {
            player.sendMessage("§eNo kits available");
            return true;
        }

        player.sendMessage("§6Available kits:");
        for (Kit kit : kits) {
            player.sendMessage("§e- " + kit.getName() + " §7(GameMode: " + kit.getGameMode() +
                ", Rounds: " + kit.getRounds() + ", Show HP: " + kit.isShowHp() + ")");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("create", "edit", "confirm", "cancel", "preview", "list").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("preview")) {
                return kitManager.getPublicKits().stream()
                    .map(Kit::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private static class KitEditSession {
        final String kitName;

        KitEditSession(String kitName) {
            this.kitName = kitName;
        }
    }

}