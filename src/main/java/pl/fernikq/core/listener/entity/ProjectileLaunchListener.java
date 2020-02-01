package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.fernikq.core.CorePlugin;

public class ProjectileLaunchListener implements Listener {

    private final CorePlugin plugin;

    public ProjectileLaunchListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        Projectile projectile = event.getEntity();
        if(!(projectile instanceof EnderPearl)) {
            return;
        }
        EnderPearl enderPearl = (EnderPearl) projectile;
        ProjectileSource source = enderPearl.getShooter();
        if(!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            user.getEnderPearls().add(enderPearl);
        });
    }
}
