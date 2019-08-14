package pl.fernikq.core.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryAction {

    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack);
}
