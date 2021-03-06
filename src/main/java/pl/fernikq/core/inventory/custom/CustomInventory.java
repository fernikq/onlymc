package pl.fernikq.core.inventory.custom;

import pl.fernikq.core.inventory.InventoryGUI;

import java.util.Map;

public class CustomInventory {

    private InventoryGUI inventoryGUI;
    private Map<Integer, String> commands;

    public CustomInventory(InventoryGUI inventoryGUI, Map<Integer, String> commands) {
        this.inventoryGUI = inventoryGUI;
        this.commands = commands;
    }

    public InventoryGUI getInventoryGUI() {
        return inventoryGUI;
    }

    public String getCommandAtSlot(int slot){
        return this.commands.get(slot);
    }
}
