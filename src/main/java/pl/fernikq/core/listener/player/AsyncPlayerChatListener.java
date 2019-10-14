package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.StringUtil;

public class AsyncPlayerChatListener implements Listener {

    private final CorePlugin plugin;

    public AsyncPlayerChatListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatFormat(AsyncPlayerChatEvent event){
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           String format = user.canByGroup(UserGroup.HELPER) ? MessagesManager.playerChatAdminFormat : MessagesManager.playerChatFormat;
           if(user.canByGroup(UserGroup.HELPER)){
               event.setMessage(ChatUtil.fixColor(event.getMessage()));
           }
           format = StringUtil.replace(format, "{LVL}", "TODO");
           format = StringUtil.replace(format, "{GUILD}", "TODO");
           format = StringUtil.replace(format, "{RANK}", user.getGroup().getPrefix());
           format = StringUtil.replace(format, "{NAME}", player.getName());
           format = StringUtil.replace(format, "{MESSAGE}", "%2$s");
           event.setFormat(ChatUtil.fixColor(format));
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event){
        if(ConfigManager.chatEnabled){
            return;
        }
        User user = this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).getOrNull();
        if(user == null){
            event.setCancelled(true);
            ChatUtil.sendMessage(event.getPlayer(), MessagesManager.error("Zglos sie do admnistracji!"));
            return;
        }
        if(user.canByGroup(UserGroup.HELPER)){
            return;
        }
        event.setCancelled(true);
        ChatUtil.sendMessage(event.getPlayer(), MessagesManager.error("Chat jest aktualnie wylaczony!"));
    }
}
