package pl.fernikq.core.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.fernikq.core.config.ConfigManager;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {

    private ItemStack itemStack;

    public ItemBuilder(Material material){
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount){
        this(material, amount, (short) 0);
    }

    public ItemBuilder(Material material, int amount, short durability){
        this.itemStack = new ItemStack(material, amount, (short) durability);
    }

    public ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public ItemBuilder setType(Material material){
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder setAmount(int amount){
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability){
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setName(String name){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(List<String> list){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(list);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder removeItemFlag(ItemFlag itemFlag){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.removeItemFlags(itemFlag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(String name){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(itemMeta.hasLore()){
            lore = itemMeta.getLore();
        }
        lore.add(name);
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder removeLore(int index){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(itemMeta.hasLore()){
            lore = itemMeta.getLore();
        }
        if(index >= lore.size()){
            return this;
        }
        lore.remove(index);
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level){
        if(this.itemStack.containsEnchantment(enchantment)){
            this.itemStack.removeEnchantment(enchantment);
        }
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder setEnchant(Map<Enchantment, Integer> enchants){
        this.itemStack.addUnsafeEnchantments(enchants);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment){
        if(!this.itemStack.containsEnchantment(enchantment)){
            return this;
        }
        this.itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setSkullOwner(String name){
        if(!ConfigManager.usePremiumHeadsInGUI){
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if(!(itemMeta instanceof SkullMeta)){
            return this;
        }
        ((SkullMeta) itemMeta).setOwner(name);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setCustomSkullOwner(String value){
        if(!ConfigManager.usePremiumHeadsInGUI){
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if(!(itemMeta instanceof SkullMeta)){
            return this;
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));
        SkullMeta headMeta = (SkullMeta) this.itemStack.getItemMeta();
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        }catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException ignored){}
        this.itemStack.setItemMeta(headMeta);
        return this;
    }

    public ItemBuilder setColor(Color color){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if(!(itemMeta instanceof LeatherArmorMeta)){
            return this;
        }
        ((LeatherArmorMeta) itemMeta).setColor(color);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack toItemStack(){
        return this.itemStack;
    }
}
