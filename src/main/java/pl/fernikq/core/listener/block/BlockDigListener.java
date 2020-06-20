/*package pl.fernikq.core.listener.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.packetWrapper.WrapperPlayClientBlockDig;
import pl.fernikq.core.CorePlugin;

@SuppressWarnings("deprecation")
public class BlockDigListener extends PacketAdapter {

    private final CorePlugin plugin;

    public BlockDigListener(CorePlugin plugin){
        super(plugin, PacketType.Play.Client.BLOCK_DIG);
        this.plugin = plugin;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        try {
            Player player = event.getPlayer();
            final WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(event.getPacket());
            final World world = player.getWorld();
            final Location location = packet.getLocation().toLocation(world);
            if(location == null){
                return;
            }
            Block block = location.getBlock();
            if(block == null){
                return;
            }
            if(!block.getType().isSolid()) {
                return;
            }
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().fakeBlockCanDestroy(user, block.getLocation());
            if(!regionFeedback.isPermit()){
                player.sendBlockChange(location, Material.BEDROCK, (byte) 1);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
*/