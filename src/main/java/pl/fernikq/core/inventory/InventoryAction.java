package pl.fernikq.core.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryAction {

    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event);
}
