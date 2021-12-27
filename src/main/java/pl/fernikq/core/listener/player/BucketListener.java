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
import java.util.UUID;
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
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(user, block.getLocation());
        if(regionFeedback == RegionFeedback.ALLOW_WATER_TRICK){
            if(!this.plugin.getLocationOfWaterBlocks().containsKey(block.getLocation())){
                event.setCancelled(true);
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
                return;
            }
            UUID uuid = this.plugin.getLocationOfWaterBlocks().get(block.getLocation());
            if(!uuid.equals(player.getUniqueId())){
                event.setCancelled(true);
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
                return;
            }
            this.plugin.getLocationOfWaterBlocks().remove(block.getLocation());
            return;
        }
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
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canUseBuckets(user, block.getLocation());
        if(regionFeedback == RegionFeedback.ALLOW_WATER_TRICK){
            Block waterBlock = block.getRelative(event.getBlockFace());
            if(waterBlock.getType() == Material.AIR || Objects.isNull(waterBlock)){
                int bucketSlot = player.getInventory().getHeldItemSlot();
                this.waterTrickMethod(player, waterBlock, bucketSlot);
                return;
            }
        }
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
    }

    private void waterTrickMethod(Player player, Block block, int bucketSlot){
        this.plugin.getLocationOfWaterBlocks().put(block.getLocation(), player.getUniqueId());
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            if(!this.plugin.getLocationOfWaterBlocks().containsKey(block.getLocation())){
                return;
            }
            UUID uuid = this.plugin.getLocationOfWaterBlocks().get(block.getLocation());
            if(!uuid.equals(player.getUniqueId())){
                return;
            }
            block.setType(Material.AIR);
            this.plugin.getLocationOfWaterBlocks().remove(block.getLocation());
            ItemStack itemStack = player.getInventory().getItem(bucketSlot);
            if(Objects.nonNull(itemStack) && itemStack.getType() == Material.BUCKET){
                itemStack.setType(Material.WATER_BUCKET);
                player.updateInventory();
            }
        }, 20);
    }
}
