package pl.fernikq.core.listener.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.StringUtil;

import java.util.concurrent.TimeUnit;

public class AsyncPlayerChatListener implements Listener {

    private final CorePlugin plugin;
    private Cache<User, Long> cache;

    public AsyncPlayerChatListener(CorePlugin plugin){
        this.plugin = plugin;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatFormat(AsyncPlayerChatEvent event){
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(this.cache.asMap().containsKey(user)){
               event.setCancelled(true);
               ChatUtil.sendMessage(player, MessagesManager.error("Poczekaj chwile przed ponownym napisaniem wiadomosci!"));
               return;
           }
           if(user.hasGuild() && event.getMessage().startsWith("!!")){
               Guild guild = user.getGuild();
               String message = ConfigManager.guildAlliesChatFormat;
               message = message.replace("{PLAYER}", player.getName());
               message = message.replace("{TAG}", guild.getTag());
               String finalMessage = message;
               this.plugin.getAllianceManager().getAllies(guild).forEach(allie -> {
                    allie.getOnlineMembers().forEach(member -> {
                        member.getUser().asPlayer().sendMessage(ChatUtil.fixColor(finalMessage) + event.getMessage().replaceFirst("!!", ""));
                    });
               });
               guild.getOnlineMembers().forEach(member -> {
                   member.getUser().asPlayer().sendMessage(ChatUtil.fixColor(finalMessage) + event.getMessage().replaceFirst("!!", ""));
               });
               event.setCancelled(true);
               return;
           }
           if(user.hasGuild() && event.getMessage().startsWith("!")){
               Guild guild = user.getGuild();
               String message = ConfigManager.guildOwnChatFormat;
               message = message.replace("{PLAYER}", player.getName());
               String finalMessage = message;
               guild.getOnlineMembers().forEach(member -> {
                   member.getUser().asPlayer().sendMessage(ChatUtil.fixColor(finalMessage) + event.getMessage().replaceFirst("!", ""));
               });
               event.setCancelled(true);
               return;
           }
           String format = user.canByGroup(UserGroup.HELPER) ? MessagesManager.playerChatAdminFormat : MessagesManager.playerChatFormat;
           if(user.canByGroup(UserGroup.HELPER)){
               event.setMessage(ChatUtil.fixColor(event.getMessage()));
           }
           format = StringUtil.replace(format, "{LVL}", user.getUserStat().getLevel());
           if(user.hasGuild()){
               format = StringUtil.replace(format, "{GUILD}", MessagesManager.playerChatGuildFormat.replace("{GUILD}", user.getGuild().getTag()));
           }else{
               format = StringUtil.replace(format, "{GUILD}", "");
           }
           format = StringUtil.replace(format, "{RANK}", user.getGroup().getPrefix());
           format = StringUtil.replace(format, "{POINTS}", MessagesManager.playerChatPointsFormat.replace("{POINTS}", Integer.toString(user.getUserStat().getPoints())));
           format = StringUtil.replace(format, "{NAME}", player.getName());
           format = StringUtil.replace(format, "{MESSAGE}", "%2$s");
           event.setFormat(ChatUtil.fixColor(format));
           if(!user.canByGroup(UserGroup.HELPER)){
               this.cache.put(user, 100L);
           }
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
