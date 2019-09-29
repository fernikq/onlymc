package pl.fernikq.core.util;

import org.bukkit.Material;
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
}
