package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.crafting.GeneratorType;
import pl.fernikq.core.listener.player.PlayerItemConsumeListener;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;
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
        if(victim.equals(damager)){
            return;
        }
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
            ItemStack magicRod = this.plugin.getGeneratorManager().getGenerator(GeneratorType.MAGIC_ROD).getItemStack();
            if(ItemUtil.isSimilar(magicRod, damager.getItemInHand(), false)){
               if(event.getDamager() instanceof Projectile){
                   Projectile projectile = (Projectile)event.getDamager();
                   if(projectile.getType() == EntityType.FISHING_HOOK){
                       this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                           PlayerUtil.punchPlayer(victim, damager.getLocation(), victim.getLocation(), 0.10);
                       }, 1);
                   }
               }
            }
            if(PlayerItemConsumeListener.cache.asMap().containsKey(victim.getUniqueId())){
                ChatUtil.sendMessage(victim, "&8[&eAntynogi&8] &fZostales przeteleportowany do gracza &e"+(this.plugin.getIncognitoManager().changeName(damagerUser, victimUser) ? "&k"+damagerUser.getName()+"&f" : damagerUser.getName()+"&f")+" przez antynogi!");
                ChatUtil.sendMessage(damager, "&8[&eAntynogi&8] &fGracz &e"+(this.plugin.getIncognitoManager().changeName(victimUser, damagerUser) ? "&k"+victimUser.getName()+"&f" : victimUser.getName()+"&f")+" zostal do ciebie przeteleportowany przez antynogi!");
                victim.teleport(damager);
                PlayerItemConsumeListener.cache.asMap().remove(victim.getUniqueId());
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
