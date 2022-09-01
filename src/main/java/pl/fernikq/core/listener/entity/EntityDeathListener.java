package pl.fernikq.core.listener.entity;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.RandomUtil;

import java.util.Objects;

public class EntityDeathListener implements Listener {

    private final CorePlugin plugin;

    public EntityDeathListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        EntityType entityType = event.getEntityType();
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if(entityType == EntityType.GIANT && StringUtils.equalsIgnoreCase(entity.getCustomName(), ChatUtil.fixColor(this.plugin.getBossManager().getGiantBossName()))){
            event.getDrops().clear();
            if(Objects.nonNull(killer)) {
                this.plugin.getBossManager().getBossDrops().stream().filter(bossDrop -> RandomUtil.getChance(bossDrop.getChance())).forEach(bossDrop -> {
                    ItemBuilder itemBuilder = new ItemBuilder(bossDrop.getItemStack().clone()).setAmount(RandomUtil.getRandInt(bossDrop.getMinAmount(), bossDrop.getMaxAmount()));
                    killer.getWorld().dropItemNaturally(entity.getLocation(), itemBuilder.toItemStack());
                });
                entity.getWorld().getNearbyEntities(entity.getLocation(), 50, 50, 50).stream()
                        .filter(nearbyEntity -> Objects.nonNull(nearbyEntity.getCustomName()) &&
                                nearbyEntity.getCustomName().equalsIgnoreCase(ChatUtil.fixColor("&c&lObronca Bossa")))
                            .forEach(nearbyEntity -> {
                                nearbyEntity.getLocation().getWorld().playEffect(nearbyEntity.getLocation(), Effect.EXPLOSION_LARGE, 2);
                                nearbyEntity.getLocation().getWorld().playSound(nearbyEntity.getLocation(), Sound.EXPLODE, 5, 1);
                                nearbyEntity.remove();
                            });
                this.plugin.getServer().getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8[&e&lBOSSY&8] &fGracz &e" + killer.getName() + " &fpokonal bestie, a ta wyrzucila na ziemie &6cenne &fprzedmioty!"));
            }
            return;
        }
        if(entityType == EntityType.ZOMBIE && StringUtils.equalsIgnoreCase(entity.getCustomName(), ChatUtil.fixColor("&c&lObronca Bossa"))){
            event.getDrops().clear();
            return;
        }
    }
}
