package pl.fernikq.core.listener.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class InventoryClickListener implements Listener {

    private final CorePlugin plugin;

    public InventoryClickListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }
        Player player = (Player)event.getWhoClicked();
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            InventoryGUI inventoryGUI = user.getInventories().get(ChatUtil.fixColor(event.getInventory().getName()));
            if(inventoryGUI != null){
                event.setCancelled(true);
                InventoryAction action = inventoryGUI.getActions().get(event.getRawSlot());
                if(action != null){
                    action.execute(player, event.getInventory(), event.getRawSlot(), event.getInventory().getItem(event.getRawSlot()));
                }
                return;
            }
        });
    }
}
