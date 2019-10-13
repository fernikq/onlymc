package pl.fernikq.core.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ItemUtil {

    public static Material getMaterial(String string){
        if(NumberUtil.isInt(string)){
            return Material.getMaterial(Integer.parseInt(string));
        }
        return Material.getMaterial(string.toUpperCase());
    }

    public static void giveItems(Player p, ItemStack... items) {
        Inventory i = p.getInventory();
        HashMap<Integer, ItemStack> notStored = i.addItem(items);
        for (Map.Entry<Integer, ItemStack> e : notStored.entrySet()) {
            p.getWorld().dropItemNaturally(p.getLocation(), (ItemStack)e.getValue());
        }
    }

    public static Map<Enchantment, Integer> getEnchantsFromString(String string){
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        String[] str = string.split(";");
        for(int i = 0; i < str.length; i++) {
            try {
                String st = str[i];
                String[] s = st.split(" ");
                String enchant = s[0].split(":")[0];
                int lvl = Integer.parseInt(s[0].split(":")[1]);
                Enchantment enchantment = EnchantManager.get(enchant);
                if(enchantment == null){
                    continue;
                }
                enchantments.put(enchantment, lvl);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return enchantments;
    }
}
