package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.incognito.IncognitoType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TitleUtil;

public class PlayerInteractEntityListener implements Listener {

    private final CorePlugin plugin;

    public PlayerInteractEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        }
    }
}
