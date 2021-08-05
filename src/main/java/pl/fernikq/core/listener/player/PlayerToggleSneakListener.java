package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

import java.util.Objects;

public class PlayerToggleSneakListener implements Listener {

    private final CorePlugin plugin;

    public PlayerToggleSneakListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDiscoArmor(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        boolean isSneaking = event.isSneaking();
        if(!this.plugin.getDiscoArmorManager().isWorking(player.getUniqueId())){
            return;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(Objects.nonNull(user) && user.getUserFight().isDuringFight()){
            return;
        }
        if(!isSneaking && this.plugin.getDiscoArmorManager().getOriginalArmor().containsKey(player.getUniqueId())){
            this.plugin.getDiscoArmorManager().restoreOriginalArmor(player);
            this.plugin.getDiscoArmorManager().getOriginalArmor().remove(player.getUniqueId());
            return;
        }
        if(isSneaking){
            this.plugin.getDiscoArmorManager().getOriginalArmor().put(player.getUniqueId(), player.getInventory().getArmorContents());
        }
    }
}
