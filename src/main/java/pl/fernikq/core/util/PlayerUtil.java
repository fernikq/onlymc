package pl.fernikq.core.util;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
}
