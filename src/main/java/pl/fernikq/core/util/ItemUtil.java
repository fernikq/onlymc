package pl.fernikq.core.util;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("deprecation")
public class ItemUtil {

    public static Material getMaterial(String string){
        Material material;
        material = NumberUtil.isInt(string) ? Material.getMaterial(Integer.parseInt(string)) : Material.getMaterial(string.toUpperCase());
        return material == null ? Material.AIR : material;
    }

    public static int getAmountOfMaterial(Inventory inv, Material material, Short durability){
        int amount = 0;
        for(ItemStack itemStack : inv.getContents()){
            if(itemStack != null && itemStack.getType() == material && itemStack.getDurability() == durability){
                amount += itemStack.getAmount();
            }
        }
        return amount;
    }

    public static int getAmountOfMaterial(Inventory inv, Material material){
        int amount = 0;
        for(ItemStack itemStack : inv.getContents()){
            if(itemStack != null && itemStack.getType() == material){
                amount += itemStack.getAmount();
            }
        }
        return amount;
    }

    public static int getAmountOfItem(Inventory inv, ItemStack item){
        int amount = 0;
        for(ItemStack itemStack : inv.getContents()){
            if(item.isSimilar(itemStack) && item != null){
                amount += itemStack.getAmount();
            }
        }
        return amount;
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

    public static void recalculateDurability(Player player, ItemStack item) {
        if(item == null){
            return;
        }
        if (item.getType().getMaxDurability() <= 0) {
            return;
        }
        int enchantLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
        short d = item.getDurability();
        if (enchantLevel > 0){
            if (100 / (enchantLevel + 1) > RandomUtil.getRandInt(0, 100)) {
                if (d == item.getType().getMaxDurability()) {
                    player.getItemInHand().setItemMeta(null);
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                } else {
                    item.setDurability((short)(d + 1));
                }
            }
        }
        else if (d == item.getType().getMaxDurability()){
            player.getInventory().clear(player.getInventory().getHeldItemSlot());
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
        } else{
            item.setDurability((short)(d + 1));
        }
    }

    public static void removeFromHand(Player p, int i) {
        if(p.getItemInHand().getAmount() > 1) {
            p.getItemInHand().setAmount(p.getItemInHand().getAmount() - i);
        }else {
            p.setItemInHand(null);
        }
    }
    public static void remove(ItemStack is, Player player, int amount)  {
        int removed = 0;
        boolean all = false;
        List<ItemStack> toRemove = new ArrayList<ItemStack>();
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if ((item != null) && (!item.getType().equals(Material.AIR)) &&
                    (item.getType().equals(is.getType())) && (item.getDurability() == is.getDurability()) &&
                    (!all) && (removed != amount)) {
                if (item.getAmount() == amount)  {
                    if (removed == 0) {
                        toRemove.add(item.clone());
                        all = true;
                        removed = item.getAmount();
                    }
                    else  {
                        int a = amount - removed;
                        ItemStack s = item.clone();
                        s.setAmount(a);
                        toRemove.add(s);
                        removed += a;
                        all = true;
                    }
                }
                else if (item.getAmount() > amount) {
                    if (removed == 0)  {
                        ItemStack s = item.clone();
                        s.setAmount(amount);
                        toRemove.add(s);
                        all = true;
                        removed = amount;
                    }
                    else {
                        int a = amount - removed;
                        ItemStack s = item.clone();
                        s.setAmount(a);
                        toRemove.add(s);
                        removed += a;
                        all = true;
                    }
                }
                else if (item.getAmount() < amount) {
                    if (removed == 0) {
                        toRemove.add(item.clone());
                        removed = item.getAmount();
                    }
                    else {
                        int a = amount - removed;
                        if (a == item.getAmount()) {
                            toRemove.add(item.clone());
                            removed += item.getAmount();
                            all = true;
                        }
                        else if (item.getAmount() > a) {
                            ItemStack s = item.clone();
                            s.setAmount(a);
                            toRemove.add(s);
                            removed += a;
                            all = true;
                        }
                        else if (item.getAmount() < a) {
                            toRemove.add(item.clone());
                            removed += item.getAmount();
                        }
                    }
                }
            }
        }
        removeItem(player, toRemove);
    }
    public static void removeItem(Player player, List<ItemStack> items) {
        if ((player == null) || (items == null) || (items.isEmpty())) {
            return;
        }
        ItemStack is;
        for (Iterator<ItemStack> localIterator = items.iterator(); localIterator.hasNext(); player.getInventory().removeItem(new ItemStack[] { is })) {
            is = (ItemStack)localIterator.next();
        }
    }
}
