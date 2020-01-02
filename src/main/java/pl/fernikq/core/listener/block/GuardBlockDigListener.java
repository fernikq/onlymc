package pl.fernikq.core.listener.block;

/*import com.mcguard.engine.events.BlockDigEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class GuardBlockDigListener implements Listener {

    private final CorePlugin plugin;

    public GuardBlockDigListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDig(BlockDigEvent event){
        try{
            Player player = event.getPlayer();
            Block block = event.getLocation().getBlock();
            Location location = event.getLocation();
            if(block == null){
                return;
            }
            if(!block.getType().isSolid()) {
                return;
            }
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroy(user, block.getLocation());
            if(!regionFeedback.isPermit()){
                player.sendBlockChange(location, Material.BEDROCK, (byte) 1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}*/
