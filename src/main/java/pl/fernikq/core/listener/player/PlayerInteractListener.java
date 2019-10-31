package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.util.ChatUtil;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class PlayerInteractListener implements Listener {

    private final CorePlugin plugin;
    List<Material> vehicles;

    public PlayerInteractListener(CorePlugin plugin){
        this.plugin = plugin;
        this.vehicles = Arrays.asList(Material.BOAT, Material.MINECART, Material.COMMAND_MINECART, Material.EXPLOSIVE_MINECART,
                Material.HOPPER_MINECART, Material.POWERED_MINECART, Material.STORAGE_MINECART);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(player.getItemInHand() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && this.vehicles.contains(player.getItemInHand().getType())){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(player, block.getLocation(), RegionProtectionType.VEHICLES);
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
                return;
            }
            return;
        }
        if(block != null && block.getTypeId() == 60 && event.getAction() == Action.PHYSICAL){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(block.getLocation(), RegionProtectionType.FARMLANDS);
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(block != null && block.getType() == Material.TNT && event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand() != null && player.getItemInHand().getType() == Material.FLINT_AND_STEEL){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(block.getLocation(), RegionProtectionType.IGNITE_TNT);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
    }
}
