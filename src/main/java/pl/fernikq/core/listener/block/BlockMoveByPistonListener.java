package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.region.GuildRegion;
import pl.fernikq.core.region.Region;
import pl.fernikq.core.region.RegionFeedback;

public class BlockMoveByPistonListener implements Listener {

    private final CorePlugin plugin;

    public BlockMoveByPistonListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onPistionExtend(BlockPistonExtendEvent event){
        Block block = event.getBlock();
        BlockFace direction = event.getDirection();
        if(this.plugin.getStoneGeneratorManager().getStoneGenerator(block.getLocation()) != null){
            event.setCancelled(true);
            return;
        }
        for(Block blockFromList : event.getBlocks()){
            if(this.plugin.getStoneGeneratorManager().getStoneGenerator(blockFromList.getRelative(direction).getLocation()) != null){
                event.setCancelled(true);
                return;
            }
        }
        if(!this.plugin.getGuildManager().getGuilds().isEmpty()) {
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
            if(guild != null && guild.getRegion().isInCenter(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
            for(Block blockFromList : event.getBlocks()){
                guild = this.plugin.getGuildManager().getGuildByLocation(blockFromList.getRelative(direction).getLocation()).getOrNull();
                if(guild != null && guild.getRegion().isInCenter(blockFromList.getRelative(direction).getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        for(Block blockFromList : event.getBlocks()) {
            for(Region region : this.plugin.getRegionManager().getRegionsByLocation(blockFromList.getRelative(direction).getLocation())) {
                if(!region.isAllowPistons()){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
