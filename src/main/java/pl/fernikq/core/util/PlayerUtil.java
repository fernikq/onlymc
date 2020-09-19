package pl.fernikq.core.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.fernikq.core.CoreAPI;
import pl.fernikq.core.config.ConfigManager;

public class PlayerUtil {

    public static Player getDamager(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof  Player){
            return (Player)e.getDamager();
        }
        if (e.getDamager() instanceof Projectile) {
            Projectile p = (Projectile)e.getDamager();
            if ((p.getShooter() instanceof Player)) {
                return (Player) p.getShooter();
            }
        }
        return null;
    }

    public static void punchEntity(Entity entity, Location from){
        Location to = entity.getLocation();
        Vector vectorFrom = new Vector(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        Vector vectorTo = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
        Vector vectorSubtract = vectorTo.subtract(vectorFrom);
        Vector vector = new Vector(vectorSubtract.getBlockX(), vectorSubtract.getY(), vectorSubtract.getBlockZ());
        entity.setVelocity(vector.multiply(1.3).setY(0.9));
    }

    public static void punchPlayer(Player player, Location to, Location from){
        Vector vectorFrom = new Vector(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        Vector vectorTo = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
        Vector vectorSubtract = vectorTo.subtract(vectorFrom);
        Vector vector = new Vector(vectorSubtract.getBlockX(), vectorSubtract.getY(), vectorSubtract.getBlockZ());
        player.setVelocity(vector.multiply(ConfigManager.punchingLinePower).setY(0.5));
    }

    public static void punchPlayer(Player player, Location to, Location from, double power){
        Vector vectorFrom = new Vector(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        Vector vectorTo = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
        Vector vectorSubtract = vectorTo.subtract(vectorFrom);
        Vector vector = new Vector(vectorSubtract.getBlockX(), vectorSubtract.getY(), vectorSubtract.getBlockZ());
        player.setVelocity(vector.multiply(power).setY(0.5));
    }

    public static int getPing(Player p) {
        try {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            return ep.ping;
        }catch(Exception ex){
            return 0;
        }
    }

    public static boolean randomTeleport(Player player, boolean solo){
        int borderSize = (int)player.getWorld().getWorldBorder().getSize()/2;
        Location randomLocation = new Location(player.getWorld(), RandomUtil.getRandInt(-borderSize, borderSize), 100, RandomUtil.getRandInt(-borderSize, borderSize));
        if(randomLocation.getBlock().getBiome().equals(Biome.DEEP_OCEAN) || randomLocation.getBlock().getBiome().equals(Biome.OCEAN)){
            return randomTeleport(player, solo);
        }
        if(CoreAPI.getPlugin().getRegionManager().isOutOfBorder(randomLocation)){
            return randomTeleport(player, solo);
        }
        int maxY = player.getWorld().getHighestBlockYAt(randomLocation);
        randomLocation.setY(maxY);
        if(randomLocation.getBlock().getType() != Material.AIR){
            return randomTeleport(player, solo);
        }
        player.teleport(randomLocation);
        if(solo){
            ItemUtil.giveItems(player, new ItemStack(Material.COOKED_BEEF, 64), new ItemStack(Material.ENDER_CHEST), new ItemStack(Material.LOG, 16));
        }
        ChatUtil.sendMessage(player, "&8>> {n}Zostales przeteleportowany w {c}losowa lokalizacje!");
        return true;
    }
}
