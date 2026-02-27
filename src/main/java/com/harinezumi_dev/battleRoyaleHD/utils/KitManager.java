package com.harinezumi_dev.duelshd.utils;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import com.harinezumi_dev.battleRoyaleHD.models.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager {
    private final BattleRoyaleHD plugin;
    private final PersistenceService persistence;

    public KitManager(DuelsHD plugin, PersistenceService persistence) {
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
        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

        Kit kit = new Kit(name, Kit.KitType.PUBLIC, player.getUniqueId(),
            contents, armor, offhand, effects);
        publicKits.put(name, kit);

        saveKitToDatabase(kit);
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
        out.writeObject(new ArrayList<>(kit.getEffects()));
        out.writeUTF(kit.getType().name());
        out.writeUTF(kit.getGameMode().name());
        out.writeBoolean(kit.isShowHp());
        out.writeInt(kit.getRounds());

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

}