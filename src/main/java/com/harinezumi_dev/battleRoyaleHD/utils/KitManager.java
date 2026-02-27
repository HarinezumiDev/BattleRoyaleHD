package com.harinezumi_dev.battleRoyaleHD.utils;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import com.harinezumi_dev.battleRoyaleHD.models.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager {
    private final BattleRoyaleHD plugin;
    private final PersistenceService persistence;
    private final Map<String, Kit> publicKits;
    private final Map<UUID, Map<String, Kit>> privateKits;

    public KitManager(BattleRoyaleHD plugin, PersistenceService persistence) {
        this.plugin = plugin;
        this.persistence = persistence;
        this.publicKits = new ConcurrentHashMap<>();
        this.privateKits = new ConcurrentHashMap<>();
    }

    public void loadKits() {
        plugin.getLogger().info("Loading kits from database...");
    }

    public void createPublicKit(String name, Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        Kit kit = new Kit(name, Kit.KitType.PUBLIC, player.getUniqueId(),
            contents, armor, offhand);
        publicKits.put(name, kit);

        saveKitToDatabase(kit);
    }
    
    public void updatePublicKit(String name, Player player) {
        Kit oldKit = publicKits.get(name);
        if (oldKit == null) return;

        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        Kit kit = new Kit(name, Kit.KitType.PUBLIC, oldKit.getOwner(),
            contents, armor, offhand);
        
        publicKits.put(name, kit);
        saveKitToDatabase(kit);
    }
    
    public Kit getKit(String name) {
        return publicKits.get(name);
    }

    public Collection<Kit> getPublicKits() {
        return publicKits.values();
    }

    public boolean publicKitExists(String name) {
        return publicKits.containsKey(name);
    }

    public void saveKitSettings(Kit kit) {
        saveKitToDatabase(kit);
    }

    private void saveKitToDatabase(Kit kit) {
        try {
            String data = serializeKit(kit);
            persistence.saveKit(kit.getOwner(), kit.getName(), data);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save kit: " + e.getMessage());
        }
    }

    private String serializeKit(Kit kit) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(byteOut);

        out.writeObject(kit.getContents());
        out.writeObject(kit.getArmor());
        out.writeObject(kit.getOffhand());
        out.writeUTF(kit.getType().name());

        out.close();
        return Base64.getEncoder().encodeToString(byteOut.toByteArray());
    }

    public void loadPlayerKits(UUID playerId) {
        persistence.loadKit(playerId, "*").thenAccept(data -> {
            if (data != null) {
                try {
                    Kit kit = deserializeKit("loaded", playerId, data);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load player kits: " + e.getMessage());
                }
            }
        });
    }
    
    private Kit deserializeKit(String name, UUID owner, String data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream in = new BukkitObjectInputStream(byteIn);

            ItemStack[] contents = (ItemStack[]) in.readObject();
            ItemStack[] armor = (ItemStack[]) in.readObject();
            ItemStack offhand = (ItemStack) in.readObject();
            Kit.KitType type = Kit.KitType.valueOf(in.readUTF());

            in.close();

            return new Kit(name, type, owner, contents, armor, offhand);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to deserialize kit: " + e.getMessage());
            return null;
        }
    }
}