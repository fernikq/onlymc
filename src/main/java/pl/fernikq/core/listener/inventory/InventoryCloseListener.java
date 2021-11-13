package pl.fernikq.core.listener.inventory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.ChatUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventoryCloseListener implements Listener {

    private final CorePlugin plugin;

    public InventoryCloseListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) {
            return;
        }
        if(event.getInventory().getName().equalsIgnoreCase(ChatUtil.fixColor("&8[ {c}&lSkarbiec gildii &8]"))) {
            Player player = (Player) event.getPlayer();
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                if(user.hasGuild()) {
                    Guild guild = user.getGuild();
                    guild.getTreasure().setItems(event.getInventory().getContents());
                }
            });
            return;
        }
        if(event.getInventory().getName().equalsIgnoreCase(ChatUtil.fixColor("&8[ {c}&lEnderchest &8]"))){
            Player player = (Player)event.getPlayer();
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
               if(user.getEnderchest().getUserEnderchest() != null) {
                   user.getEnderchest().getUserEnderchest().getEnderchest().setItems(event.getInventory().getContents());
               }
            });
            return;
        }
    }

    @EventHandler
    public void onGuard(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        int currentSlot = player.getInventory().getHeldItemSlot();
        player.getInventory().setHeldItemSlot(currentSlot > 0 ? 0 : currentSlot + 1);
        player.getInventory().setHeldItemSlot(currentSlot);
    }
}
