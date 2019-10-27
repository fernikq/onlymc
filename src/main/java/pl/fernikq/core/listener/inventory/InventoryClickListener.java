package pl.fernikq.core.listener.inventory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventoryClickListener implements Listener {

    private final CorePlugin plugin;
    private Cache<UUID, Long> cooldown;

    public InventoryClickListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.cooldown = CacheBuilder.newBuilder().expireAfterWrite(300, TimeUnit.MILLISECONDS).build();
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
                if(inventoryGUI.isCancelling()){
                    event.setCancelled(true);
                }
                if(cooldown.asMap().containsKey(player.getUniqueId()) && ConfigManager.limitInventoryClicks){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie klikaj tak szybko w ekwipunek!"));
                    return;
                }
                if(ConfigManager.limitInventoryClicks){
                    cooldown.put(player.getUniqueId(), 300L);
                }
                InventoryAction action = inventoryGUI.getActions().get(event.getRawSlot());
                if(action != null){
                    action.execute(player, event.getInventory(), event.getRawSlot(), event.getInventory().getItem(event.getRawSlot()));
                }
                return;
            }
        });
    }
}
