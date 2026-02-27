package com.harinezumi_dev.duelshd.model;

//import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.potion.PotionEffect;
import java.util.List;
import java.util.UUID;

public class Kit {

    private final String name;
    private final KitType type;
    private final UUID owner;
    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack offhand;
    // private final List<PotionEffect> effects;

    public Kit(String name, KitType type, UUID owner, ItemStack[] contents,
               ItemStack[] armor, ItemStack offhand) { //List<PotionEffect> effects
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.contents = contents;
        this.armor = armor;
        this.offhand = offhand;
        //this.effects = effects;
    }

    public String getName() {
        return name;
    }

    public KitType getType() {
        return type;
    }

    public UUID getOwner() {
        return owner;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffhand() {
        return offhand;
    }

    /*public List<PotionEffect> getEffects() {
        return effects;
    }*/

    public void applyTo(Player player) {
        player.getInventory().setContents(contents.clone());
        player.getInventory().setArmorContents(armor.clone());
        player.getInventory().setItemInOffHand(offhand != null ? offhand.clone() : null);

        //player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        //effects.forEach(player::addPotionEffect);
    }

    public boolean canUse(Player player) {
        if (type == KitType.PUBLIC) {
            return player.hasPermission("battleRoyaleHD.kit.use." + name);
        }
        return owner.equals(player.getUniqueId());
    }

    public enum KitType {
        PUBLIC,
        PRIVATE
    }
}
