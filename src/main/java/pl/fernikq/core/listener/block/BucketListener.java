package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;

public class BucketListener implements Listener {

    private final CorePlugin plugin;

    public BucketListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(player, block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEmptyBucket(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(player, block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
    }
}
