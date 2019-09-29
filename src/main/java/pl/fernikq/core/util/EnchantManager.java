package pl.fernikq.core.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EnchantManager {

    private static final HashMap<String, Enchantment> enchants;

    static {
        enchants = new HashMap<String, Enchantment>();
        EnchantManager.enchants.put("protection".toUpperCase(), Enchantment.PROTECTION_ENVIRONMENTAL);
        EnchantManager.enchants.put("prot".toUpperCase(), Enchantment.PROTECTION_ENVIRONMENTAL);
        EnchantManager.enchants.put("protectionenvironmental".toUpperCase(), Enchantment.PROTECTION_ENVIRONMENTAL);
        EnchantManager.enchants.put("explosionprotection".toUpperCase(), Enchantment.PROTECTION_EXPLOSIONS);
        EnchantManager.enchants.put("expprotection".toUpperCase(), Enchantment.PROTECTION_EXPLOSIONS);
        EnchantManager.enchants.put("expprot".toUpperCase(), Enchantment.PROTECTION_EXPLOSIONS);
        EnchantManager.enchants.put("featherfalling".toUpperCase(), Enchantment.PROTECTION_FALL);
        EnchantManager.enchants.put("featherfall".toUpperCase(), Enchantment.PROTECTION_FALL);
        EnchantManager.enchants.put("fallprotection".toUpperCase(), Enchantment.PROTECTION_FALL);
        EnchantManager.enchants.put("fallprot".toUpperCase(), Enchantment.PROTECTION_FALL);
        EnchantManager.enchants.put("fireprot".toUpperCase(), Enchantment.PROTECTION_FIRE);
        EnchantManager.enchants.put("fireprotection".toUpperCase(), Enchantment.PROTECTION_FIRE);
        EnchantManager.enchants.put("projectileprotection".toUpperCase(), Enchantment.PROTECTION_PROJECTILE);
        EnchantManager.enchants.put("projectileprot".toUpperCase(), Enchantment.PROTECTION_PROJECTILE);
        EnchantManager.enchants.put("projprot".toUpperCase(), Enchantment.PROTECTION_PROJECTILE);
        EnchantManager.enchants.put("thorns".toUpperCase(), Enchantment.THORNS);

        EnchantManager.enchants.put("damegeall".toUpperCase(), Enchantment.DAMAGE_ALL);
        EnchantManager.enchants.put("damage".toUpperCase(), Enchantment.DAMAGE_ALL);
        EnchantManager.enchants.put("sharpness".toUpperCase(), Enchantment.DAMAGE_ALL);
        EnchantManager.enchants.put("sharp".toUpperCase(), Enchantment.DAMAGE_ALL);
        EnchantManager.enchants.put("knockback".toUpperCase(), Enchantment.KNOCKBACK);
        EnchantManager.enchants.put("knock".toUpperCase(), Enchantment.KNOCKBACK);
        EnchantManager.enchants.put("fireaspect".toUpperCase(), Enchantment.FIRE_ASPECT);
        EnchantManager.enchants.put("fire".toUpperCase(), Enchantment.FIRE_ASPECT);
        EnchantManager.enchants.put("smite".toUpperCase(), Enchantment.DAMAGE_UNDEAD);
        EnchantManager.enchants.put("baneofarthropods".toUpperCase(), Enchantment.DAMAGE_ARTHROPODS);
        EnchantManager.enchants.put("ardamage".toUpperCase(), Enchantment.DAMAGE_ARTHROPODS);
        EnchantManager.enchants.put("mobloot".toUpperCase(), Enchantment.LOOT_BONUS_MOBS);
        EnchantManager.enchants.put("mobbonusloot".toUpperCase(), Enchantment.LOOT_BONUS_MOBS);

        EnchantManager.enchants.put("fortune".toUpperCase(), Enchantment.LOOT_BONUS_BLOCKS);
        EnchantManager.enchants.put("lootbonusblocks".toUpperCase(), Enchantment.LOOT_BONUS_BLOCKS);
        EnchantManager.enchants.put("digspeed".toUpperCase(), Enchantment.DIG_SPEED);
        EnchantManager.enchants.put("efficiency".toUpperCase(), Enchantment.DIG_SPEED);
        EnchantManager.enchants.put("durability".toUpperCase(), Enchantment.DURABILITY);
        EnchantManager.enchants.put("dura".toUpperCase(), Enchantment.DURABILITY);
        EnchantManager.enchants.put("unbreaking".toUpperCase(), Enchantment.DURABILITY);
        EnchantManager.enchants.put("silktouch".toUpperCase(), Enchantment.SILK_TOUCH);

        EnchantManager.enchants.put("power".toUpperCase(), Enchantment.ARROW_DAMAGE);
        EnchantManager.enchants.put("arrowdamage".toUpperCase(), Enchantment.ARROW_DAMAGE);
        EnchantManager.enchants.put("punch".toUpperCase(), Enchantment.ARROW_KNOCKBACK);
        EnchantManager.enchants.put("arrowknockback".toUpperCase(), Enchantment.ARROW_KNOCKBACK);
        EnchantManager.enchants.put("arrowknock".toUpperCase(), Enchantment.ARROW_KNOCKBACK);
        EnchantManager.enchants.put("flame".toUpperCase(), Enchantment.ARROW_FIRE);
        EnchantManager.enchants.put("arrowfire".toUpperCase(), Enchantment.ARROW_FIRE);
        EnchantManager.enchants.put("infinite".toUpperCase(), Enchantment.ARROW_INFINITE);
        EnchantManager.enchants.put("infinity".toUpperCase(), Enchantment.ARROW_INFINITE);
        EnchantManager.enchants.put("depthstrider".toUpperCase(), Enchantment.DEPTH_STRIDER);
        EnchantManager.enchants.put("waterbreathing".toUpperCase(), Enchantment.OXYGEN);
        EnchantManager.enchants.put("breathing".toUpperCase(), Enchantment.OXYGEN);
        EnchantManager.enchants.put("oxygen".toUpperCase(), Enchantment.OXYGEN);
        EnchantManager.enchants.put("respiration".toUpperCase(), Enchantment.OXYGEN);
        EnchantManager.enchants.put("waterworker".toUpperCase(), Enchantment.WATER_WORKER);
        EnchantManager.enchants.put("luck".toUpperCase(), Enchantment.LUCK);
        EnchantManager.enchants.put("luckofthesea".toUpperCase(), Enchantment.LUCK);
        EnchantManager.enchants.put("lure".toUpperCase(), Enchantment.LURE);

    }
    @SuppressWarnings("deprecation")
    public static Enchantment get(final String name) {
        Enchantment enchant;
        if(NumberUtil.isInt(name)) {
            enchant = Enchantment.getById(Integer.valueOf(name));
        }else {
            enchant = Enchantment.getByName(name.toUpperCase());
        }
        if(enchant != null){
            return enchant;
        }
        return EnchantManager.enchants.get(name.toUpperCase());
    }
    public static HashMap<String, Enchantment> getEnchants() {
        return EnchantManager.enchants;
    }

    public static boolean canRepair(ItemStack itemStack){
        return ((itemStack != null) && (itemStack.getType() != Material.AIR) && (Enchantment.DURABILITY.canEnchantItem(itemStack)));
    }
}
