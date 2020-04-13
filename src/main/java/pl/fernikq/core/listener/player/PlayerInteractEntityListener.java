package pl.fernikq.core.listener.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.incognito.IncognitoType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.fernikq.core.util.TitleUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerInteractEntityListener implements Listener {

    private final CorePlugin plugin;
    private Cache<UUID, Long> cooldown;

    public PlayerInteractEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.cooldown = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(entity.getType() == EntityType.ITEM_FRAME) {
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(user, entity.getLocation(), true);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(entity.getType() == EntityType.PLAYER){
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            User target = this.plugin.getUserManager().getUser(entity.getUniqueId()).getOrNull();
            if(!target.getIncognito().getShowNickName().equals(IncognitoType.ALL) && user.canByGroup(UserGroup.HELPER)){
                ChatUtil.sendMessage(player, "&8>> {n}Nick gracza&8: {c}"+target.getName());
                TitleUtil.sendActionBar(player, ChatUtil.fixColor("&8>> {n}Nick gracza&8: {c}"+target.getName()));
                return;
            }
            if(player.isSneaking()){
                if(cooldown.asMap().containsKey(player.getUniqueId())){
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 60000);
                ChatUtil.sendMessage(player, "&8>> {n}Zglosiles gracza do {c}administracji&8!");
                Bukkit.getOnlinePlayers().forEach(online -> {
                    this.plugin.getUserManager().getUser(online.getUniqueId()).filter(admin -> admin.canByGroup(UserGroup.HELPER)).peek(admin -> {
                        admin.asPlayer().sendMessage(ChatUtil.fixColor(MessagesManager.helpopFormat.replace("{NICK}", player.getName())) + "Zglaszam cheatera jego nick to: "+target.getName());
                    });
                });
                return;
            }
        }
    }
}
