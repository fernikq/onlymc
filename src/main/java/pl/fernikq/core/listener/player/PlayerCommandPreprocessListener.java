package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class PlayerCommandPreprocessListener implements Listener {

    private final CorePlugin plugin;

    public PlayerCommandPreprocessListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        String command = event.getMessage().split(" ")[0];
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canProccessCommand(user, player.getLocation().getBlock().getLocation(), command);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        HelpTopic helpTopic = Bukkit.getHelpMap().getHelpTopic(command);
        if(helpTopic == null){
            ChatUtil.sendMessage(player, this.plugin.getSimpleCommandManager().getHelpCommandMessage());
            event.setCancelled(true);
            return;
        }
    }
}
