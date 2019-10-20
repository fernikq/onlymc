package pl.fernikq.core.listener.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.fernikq.packetWrapper.WrapperPlayClientBlockDig;
import pl.fernikq.core.CorePlugin;

public class BlockDigListener extends PacketAdapter {

    private final CorePlugin plugin;

    public BlockDigListener(CorePlugin plugin){
        super(plugin, PacketType.Play.Client.BLOCK_DIG);
        this.plugin = plugin;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPacketReceiving(final PacketEvent event) {
        try {
            Player player = event.getPlayer();
            final WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(event.getPacket());
            final World world = Bukkit.getWorlds().get(0);
            final Location location = packet.getLocation().toLocation(world);
            if(!location.getBlock().getType().isSolid()) {
                return;
            }
            player.sendBlockChange(location, Material.BEDROCK, (byte) 1);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
