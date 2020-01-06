package pl.fernikq.core.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
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

    public static void punchPlayer(Player player, Location to, Location from){
        Vector vectorFrom = new Vector(from.getBlockX(), from.getBlockY(), from.getBlockZ());
        Vector vectorTo = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
        Vector vectorSubtract = vectorTo.subtract(vectorFrom);
        Vector vector = new Vector(vectorSubtract.getBlockX(), vectorSubtract.getY(), vectorSubtract.getBlockZ());
        player.setVelocity(vector.multiply(ConfigManager.punchingLinePower).setY(0.5));
    }

    public static int getPing(Player p) {
        CraftPlayer cp = (CraftPlayer)p;
        EntityPlayer ep = cp.getHandle();
        return ep.ping;
    }
}
