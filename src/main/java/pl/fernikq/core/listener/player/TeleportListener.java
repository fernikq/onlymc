package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class TeleportListener implements Listener {

    private final CorePlugin plugin;

    public TeleportListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPearlTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        Location location = event.getTo();
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL){
            return;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canThrowPearls(user, location);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            ItemUtil.giveItems(player, new ItemStack(Material.ENDER_PEARL));
            return;
        }
    }
}
