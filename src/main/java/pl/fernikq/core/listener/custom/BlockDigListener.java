package pl.fernikq.core.listener.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;

@SuppressWarnings("deprecation")
public class BlockDigListener implements Listener {

    private final CorePlugin plugin;

    public BlockDigListener(CorePlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDig(BlockDigEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        if(!block.getType().isSolid()) {
            return;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().fakeBlockCanDestroy(user, block.getLocation());
        if(!regionFeedback.isPermit()){
            player.sendBlockChange(location, Material.BEDROCK, (byte) 1);
        }
    }
}
