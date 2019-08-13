package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class PlayerQuitListener implements Listener {

    private final CorePlugin plugin;

    public PlayerQuitListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        User user = this.plugin.getUserManager().getUser(player);
        //TODO update
        this.plugin.getTagManager().removeTag(player);
    }
}
