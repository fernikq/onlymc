package pl.fernikq.core.customenchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomEnchantManager {

    private final Map<CustomEnchantItemEnum, List<Enchantment>> enchantmentsByItemType = new HashMap<>();

    public CustomEnchantManager(){
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.SWORD, Arrays.asList(
                Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS,
                Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS,
                Enchantment.DURABILITY
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.BOW, Arrays.asList(
                Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_FIRE,
                Enchantment.ARROW_INFINITE, Enchantment.DURABILITY
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.HELMET, Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_PROJECTILE, Enchantment.OXYGEN, Enchantment.WATER_WORKER,
                Enchantment.DURABILITY, Enchantment.THORNS
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.CHESTPLATE, Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_PROJECTILE, Enchantment.THORNS, Enchantment.DURABILITY
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.LEGGINGS, Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_PROJECTILE, Enchantment.THORNS, Enchantment.DURABILITY
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.BOOTS, Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_PROJECTILE, Enchantment.DEPTH_STRIDER, Enchantment.DURABILITY,
                Enchantment.THORNS
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.SHOVEL, Arrays.asList(
                Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY,
                Enchantment.LOOT_BONUS_BLOCKS
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.PICKAXE, Arrays.asList(
                Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY,
                Enchantment.LOOT_BONUS_BLOCKS
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.AXE, Arrays.asList(
                Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH, Enchantment.DURABILITY,
                Enchantment.LOOT_BONUS_BLOCKS
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.HOE, Arrays.asList(
                Enchantment.DURABILITY
        ));
        this.enchantmentsByItemType.put(CustomEnchantItemEnum.FISHING_ROD, Arrays.asList(
                Enchantment.LUCK, Enchantment.LURE
        ));
    }

    public List<Enchantment> enchantmentsByItemType(CustomEnchantItemEnum customEnchantItemEnum){
        return new ArrayList<>(this.enchantmentsByItemType.get(customEnchantItemEnum));
    }

    public CustomEnchantItemEnum getTypeOfTheItem(ItemStack itemStack){
        String itemName = itemStack.getType().name();
        if(itemName.contains("SWORD")) return CustomEnchantItemEnum.SWORD;
        if(itemName.equalsIgnoreCase("BOW")) return CustomEnchantItemEnum.BOW;
        if(itemName.contains("HELMET")) return CustomEnchantItemEnum.HELMET;
        if(itemName.contains("CHESTPLATE")) return CustomEnchantItemEnum.CHESTPLATE;
        if(itemName.contains("LEGGINGS")) return CustomEnchantItemEnum.LEGGINGS;
        if(itemName.contains("BOOTS")) return CustomEnchantItemEnum.BOOTS;
        if(itemName.contains("SPADE")) return CustomEnchantItemEnum.SHOVEL;
        if(itemName.contains("PICKAXE")) return CustomEnchantItemEnum.PICKAXE;
        if(itemName.contains("AXE")) return CustomEnchantItemEnum.AXE;
        if(itemName.contains("HOE")) return CustomEnchantItemEnum.HOE;
        if(itemName.equalsIgnoreCase("FISHING_ROD")) return CustomEnchantItemEnum.FISHING_ROD;
        return null;
    }
}
