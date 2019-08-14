package pl.fernikq.core.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashMap;
import java.util.Map;

public class InventoryGUI {

    private Inventory inventory;
    private Map<Integer, InventoryAction> actions;
    private int slots;

    public InventoryGUI(String name, int rows){
        this.inventory = Bukkit.createInventory(null, rows * 9, ChatUtil.fixColor(name));
        this.actions = new HashMap<>();
        this.slots = rows * 9;
    }

    public void setItem(int slot, ItemStack itemStack){
        if(!isCorrectSlot(slot)){
            return;
        }
        this.inventory.setItem(slot, itemStack);
        this.actions.put(slot, null);
    }

    public void setItem(int slot, ItemStack itemStack, InventoryAction action){
        if(!isCorrectSlot(slot)){
            return;
        }
        this.inventory.setItem(slot, itemStack);
        this.actions.put(slot, action);
    }

    public void addItem(ItemStack itemStack){
        for(int i = 0; i < this.inventory.getSize(); i++){
            if(this.inventory.getItem(i) == null || this.inventory.getItem(i).getType() == Material.AIR){
                this.inventory.setItem(i, itemStack);
                this.actions.put(i, null);
                return;
            }
        }
    }

    public void addItem(ItemStack itemStack, InventoryAction action){
        for(int i = 0; i < this.inventory.getSize(); i++){
            if(this.inventory.getItem(i) == null || this.inventory.getItem(i).getType() == Material.AIR){
                this.inventory.setItem(i, itemStack);
                this.actions.put(i, action);
                return;
            }
        }
    }

    public void setEmptyItem(ItemStack is){
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if(this.inventory.getItem(i) == null || this.inventory.getItem(i).getType() == Material.AIR){
                this.inventory.setItem(i, is);
                this.actions.put(i, null);
            }
        }
    }

    public void openInventory(Player player){
        player.openInventory(this.inventory);
    }

    public boolean isCorrectSlot(int slot){
        return this.slots <= slot;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSlots() {
        return slots;
    }

    public Map<Integer, InventoryAction> getActions() {
        return actions;
    }
}
