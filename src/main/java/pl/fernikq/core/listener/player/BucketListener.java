package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(user, block.getLocation(), true);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEmptyBucket(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        int bucketSlot = player.getInventory().getHeldItemSlot();
        Block block = event.getBlockClicked();
        Block waterBlock = block.getRelative(event.getBlockFace());
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(user, block.getLocation(), false);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
        if(Objects.nonNull(guild) && !guild.equals(user.getGuild())){
            this.plugin.getLocationOfWaterBlocks().add(waterBlock.getLocation());
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                waterBlock.setType(Material.AIR);
                this.plugin.getLocationOfWaterBlocks().remove(waterBlock.getLocation());
                ItemStack itemStack = player.getInventory().getItem(bucketSlot);
                if(Objects.nonNull(itemStack) && itemStack.getType() == Material.BUCKET){
                    itemStack.setType(Material.WATER_BUCKET);
                    player.updateInventory();
                }
            }, 20);
        }
    }
}
