package pl.fernikq.core.abyss;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.AbyssAction;
import pl.fernikq.core.inventory.actions.guild.GuildPanelAction;
import pl.fernikq.core.inventory.enums.AbyssActionType;
import pl.fernikq.core.inventory.enums.guild.GuildPanelActionType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbyssManager {

    private final CorePlugin plugin;
    private final Map<String, InventoryGUI> inventories;
    private final List<InventoryGUI> inventoryList;
    private boolean opened;

    public AbyssManager(CorePlugin plugin){
        this.plugin = plugin;
        this.inventories = new HashMap<>();
        this.inventoryList = new ArrayList<>();
        this.opened = false;
    }

    public InventoryGUI createInventoryGui(List<ItemStack> itemStacks, int page){
        InventoryGUI gui = new InventoryGUI(ChatUtil.fixColor("&8[ {c}&lOtchlan "+page+" &8]"), 6, false);
        List<ItemStack> items = new ArrayList<>(itemStacks);
        for(int i = 0; i < 45; i++) {
            if(items.size() > 0 && i <= items.size() - 1){
                ItemStack itemStack = items.get(i);
                itemStacks.remove(itemStack);
                gui.addItem(itemStack);
                continue;
            }
            break;
        }
        gui.setItem(53, new ItemBuilder(Material.SKULL_ITEM).setDurability((short)3).setSkullOwner("MHF_ArrowRight").setName(ChatUtil.fixColor("&a&lDalej")).toItemStack(), new AbyssAction(this.plugin, AbyssActionType.NEXT, page - 1));
        gui.setItem(52, new ItemBuilder(Material.SKULL_ITEM).setDurability((short)3).setSkullOwner("MHF_ArrowLeft").setName(ChatUtil.fixColor("&c&lWstecz")).toItemStack(), new AbyssAction(this.plugin, AbyssActionType.BACK, page - 1));
        return gui;
    }

    public void createInventories(List<ItemStack> itemStacks){
        this.inventories.clear();
        this.inventoryList.clear();
        this.opened = true;
        int page = 1;
        while(!itemStacks.isEmpty()){
            InventoryGUI inventoryGUI = createInventoryGui(itemStacks, page);
            this.inventories.putIfAbsent(inventoryGUI.getInventory().getName(), inventoryGUI);
            this.inventoryList.add(inventoryGUI);
            page++;
        }
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public Map<String, InventoryGUI> getInventories() {
        return inventories;
    }

    public List<InventoryGUI> getInventoriesToList(){
        return new ArrayList<>(this.inventoryList);
    }
}
