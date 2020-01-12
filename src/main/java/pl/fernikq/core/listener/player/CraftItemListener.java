package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;

public class CraftItemListener implements Listener {

    private final CorePlugin plugin;

    public CraftItemListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if(event.getInventory().getType() != InventoryType.WORKBENCH){
            return;
        }
        if(event.getInventory().getResult() == null){
            return;
        }
        ItemStack result = event.getInventory().getResult();
        Player player = (Player)event.getWhoClicked();
        if(result.getType() == Material.JUKEBOX){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, MessagesManager.error("Tego przedmiotu nie mozna stworzyc!"));
            return;
        }
    }
}
