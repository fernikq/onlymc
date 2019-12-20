package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.PlayerUtil;

public class EntityDamageByEntityListener implements Listener {

    private final CorePlugin plugin;

    public EntityDamageByEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(EntityDamageByEntityEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER){
            return;
        }
        Player damager = PlayerUtil.getDamager(event);
        if(damager == null){
            return;
        }
        Player victim = (Player)event.getEntity();
        User damagerUser = this.plugin.getUserManager().getUser(damager.getUniqueId()).getOrNull();
        User victimUser = this.plugin.getUserManager().getUser(victim.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canHurt(damagerUser, victimUser);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(damager, regionFeedback.getFeedbackMessage());
            return;
        }
        if(!damager.equals(victim)){
            UserFight victimFight = victimUser.getUserFight();
            UserFight damagerFight = damagerUser.getUserFight();
            victimFight.setLastAttacker(damagerUser);
            victimFight.setLastAttackTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000));
            victimFight.setAntylogoutTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000));
            damagerFight.setLastAttacker(victimUser);
            damagerFight.setLastAttackTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000));
            damagerFight.setAntylogoutTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000));
            this.plugin.getFightManager().getUsersDuringFight().add(damagerUser);
            this.plugin.getFightManager().getUsersDuringFight().add(victimUser);
            Damage damage = victimFight.getDamageByUser(damagerUser);
            if(damage == null){
                damage = new Damage(damagerUser, event.getDamage());
                victimFight.getDamageMap().put(damagerUser, damage);
            }else{
                damage.setDamage(damage.getDamage() + event.getDamage());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFrame(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.ITEM_FRAME){
            return;
        }
        Player damager = PlayerUtil.getDamager(event);
        if(damager == null) {
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(null, event.getEntity().getLocation(), false);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        User damagerUser = this.plugin.getUserManager().getUser(damager.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(damagerUser, event.getEntity().getLocation(), true);
        if(!regionFeedback.isPermit()) {
            event.setCancelled(true);
            return;
        }
    }
}
