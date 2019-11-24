package pl.fernikq.core.inventory.guild;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.util.Arrays;

public class GuildInventory {

    private final CorePlugin plugin;
    private ItemStack blank;
    private ItemStack color;
    private ItemStack backGlass;
    private ItemStack backBarrier;

    public GuildInventory(CorePlugin plugin){
        this.plugin = plugin;
        blank = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).setName(" ").toItemStack();
        color = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(" ").toItemStack();
        backGlass = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        backBarrier = new ItemBuilder(new ItemStack(Material.BARRIER, 1)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
    }

    public InventoryGUI guildItems(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lPrzedmioty na gildie &8]", 3, true);
        user.addInventory(gui);
        int slot = 9;
        if(user.canByGroup(UserGroup.VIP)) {
            for(String item : ConfigManager.guildVipItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                int have = ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), material, data);
                String name = itemInfo[3];
                gui.setItem(slot, new ItemBuilder(material, amount, data).setName(ChatUtil.fixColor("{c}"+name)).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}"+have+"&8/{c}"+amount))).toItemStack());
                if(have > amount){
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                }else{
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                }
                slot++;
            }
        }else{
            for(String item : ConfigManager.guildPlayerItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                int have = ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), material, data);
                String name = itemInfo[3];
                gui.setItem(slot, new ItemBuilder(material, amount, data).setName(ChatUtil.fixColor("{c}"+name)).toItemStack());
                if(have > amount){
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                }else{
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                }
                slot++;
            }
        }
        return gui;
    }
}
