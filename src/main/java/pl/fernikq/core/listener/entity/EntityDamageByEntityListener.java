package pl.fernikq.core.listener.entity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.GeneratorType;
import pl.fernikq.core.listener.player.PlayerItemConsumeListener;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
import pl.fernikq.core.util.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EntityDamageByEntityListener implements Listener {

    private final CorePlugin plugin;

    private final ItemStack[] littleZombiesArmor = new ItemStack[]{new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).toItemStack()};
    private final Cache<UUID, Integer> clicksPerSecond;

    public EntityDamageByEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.clicksPerSecond = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();
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
        if(ConfigManager.freezeTime > System.currentTimeMillis()){
            event.setCancelled(true);
            ChatUtil.sendMessage(damager, "&8[&b&lZamrozenie&8] &fNie mozesz atakowac graczy!");
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
        if(this.plugin.getProtectionManager().isProtected(damager.getUniqueId())){
            ChatUtil.sendMessage(damager, MessagesManager.error("Nie mozesz zaatakowac tego gracza, poniewaz posiadasz ochrone! Wylacz ja poprzez komende /ochrona"));
            event.setCancelled(true);
            return;
        }
        if(this.plugin.getProtectionManager().isProtected(victim.getUniqueId())){
            ChatUtil.sendMessage(damager, MessagesManager.error("Podany gracz posiada ochrone startowa!"));
            event.setCancelled(true);
            return;
        }
        if(!damager.equals(victim)){
            UserFight victimFight = victimUser.getUserFight();
            UserFight damagerFight = damagerUser.getUserFight();
            victimFight.setLastAttacker(damagerUser);
            victimFight.setLastAttackTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000L));
            victimFight.setAntylogoutTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000L));
            damagerFight.setLastAttacker(victimUser);
            damagerFight.setLastAttackTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000L));
            damagerFight.setAntylogoutTime(System.currentTimeMillis() + (ConfigManager.playerFightTime * 1000L));
            this.plugin.getFightManager().getUsersDuringFight().add(damagerUser);
            this.plugin.getFightManager().getUsersDuringFight().add(victimUser);
            if(damager.isSneaking() && this.plugin.getDiscoArmorManager().isWorking(damager.getUniqueId())){
                if(this.plugin.getDiscoArmorManager().getOriginalArmor().containsKey(damager.getUniqueId())){
                    this.plugin.getDiscoArmorManager().restoreOriginalArmor(damager);
                }
            }
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
            /*Integer clicksPerSecond = this.clicksPerSecond.asMap().getOrDefault(damager.getUniqueId(), null);
            if(clicksPerSecond != null && clicksPerSecond.intValue() >= ConfigManager.allowedClickPerSecond){
                ChatUtil.sendMessage(damager, MessagesManager.error("Za szybko uderzasz!"));
                event.setCancelled(true);
                return;
            }
            this.clicksPerSecond.put(damager.getUniqueId(), Objects.isNull(clicksPerSecond) ? 1 : (clicksPerSecond.intValue() + 1));*/
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

    @EventHandler
    public void onBoss(EntityDamageByEntityEvent event){
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        if(victim.getType() != EntityType.GIANT){
            return;
        }
        if(damager.getType() != EntityType.PLAYER){
            return;
        }
        if(StringUtils.equalsIgnoreCase(victim.getCustomName(), ChatUtil.fixColor(this.plugin.getBossManager().getGiantBossName()))){
            Player player = (Player)damager;
            if(RandomUtil.getChance(35) && !player.isDead()){
                PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(((CraftEntity)victim).getHandle(), 0);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutAnimation);
                player.playEffect(EntityEffect.HURT);
                player.damage(RandomUtil.getRandInt(2, 5));
            }
            if(RandomUtil.getChance(8)){
                victim.getNearbyEntities(8, 5, 8).stream().filter(entity -> entity.getType() == EntityType.PLAYER && !entity.isDead()).forEach(entity -> {
                    PlayerUtil.punchEntity(entity, victim.getLocation());
                    ((Player)entity).playSound(victim.getLocation(), Sound.EXPLODE, 10, 1);
                });
            }
            if(RandomUtil.getChance(ConfigManager.bossGuardSpawnChance)){
                int count = 1;
                List<Entity> players = victim.getNearbyEntities(8, 5, 8).stream().filter(entity -> entity.getType() == EntityType.PLAYER).collect(Collectors.toList());
                if(players.size() > count) count = players.size();
                players.forEach(entity -> ((Player)entity).playSound(entity.getLocation(), Sound.AMBIENCE_THUNDER, 10, 1));
                for(int i = 0; i < RandomUtil.getRandInt(count, count * 2); i++){
                    Zombie zombie = (Zombie) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.ZOMBIE);
                    zombie.setBaby(true);
                    zombie.setCustomName(ChatUtil.fixColor("&c&lObronca Bossa"));
                    zombie.setCustomNameVisible(true);
                    zombie.setMaxHealth(40.0);
                    zombie.setHealth(40.0);
                    zombie.setCanPickupItems(false);
                    zombie.getEquipment().setArmorContents(this.littleZombiesArmor);
                    zombie.getEquipment().setBootsDropChance(100);
                    zombie.getEquipment().setItemInHand(new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.FIRE_ASPECT, 1).addEnchant(Enchantment.DAMAGE_ALL, 3).toItemStack());
                }
            }
            return;
        }
    }
}
