package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.fernikq.core.CorePlugin;

public class PlayerDamageListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDamageListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGodMode(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        this.plugin.getUserManager().getUser(event.getEntity().getUniqueId()).peek(user -> {
           if(user.isGodMode()){
               event.setCancelled(true);
           }
        });
    }
}
