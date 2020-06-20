package pl.fernikq.core.listener.player;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.check.PlayerCheckUtil;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.backup.Backup;
import pl.fernikq.core.user.backup.BackupBuilder;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.PlayerUtil;

import java.util.Objects;

public class PlayerQuitListener implements Listener {

    private final CorePlugin plugin;

    public PlayerQuitListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage(ChatUtil.fixColor(MessagesManager.playerQuitMessage.replace("{PLAYER}", player.getName())));
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            user.getUserStat().setSpentTime(user.getUserStat().getSpentTime() + (System.currentTimeMillis() - user.getUserStat().getJoinTime()));
            user.getUserStat().setJoinTime(0L);
            if(user.getUserFight().isDuringFight()){
                user.setLogout(true);
                player.setHealth(0.0);
                user.getUserStat().setLogouts(user.getUserStat().getLogouts() + 1);
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_LOGOUTS).setSorted(false));
                if(user.hasGuild()){
                    this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_LOGOUTS).setSorted(false));
                }
                String message = MessagesManager.playerFightLogoutMessage;
                message = message.replace("{PLAYER}", user.getName());
                String finalMessage = message;
                this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> Objects.nonNull(onlineUser) && onlineUser.getUserChat().isFightMessages()).forEach(onlineUser -> ChatUtil.sendMessage(onlineUser.asPlayer(), finalMessage));
            }
            this.plugin.runAsync(() -> {
                this.plugin.getUserManager().updateUser(user);
                if(user.hasGuild()){
                    this.plugin.getGuildManager().updateGuild(user.getGuild());
                }
            });
            if(user.getSidebar().hasSidebar()){
                user.getSidebar().remove();
            }
            user.setScoreboard(null);
        });
        this.plugin.getTagManager().removeTag(player);
        this.plugin.getVanishManager().removeVanished(player);
        if(PlayerCheckUtil.getPlayerSet().contains(player)){
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), ConfigManager.playerCheckLogoutCommand.replace("{PLAYER}", player.getName()));
            PlayerCheckUtil.getPlayerSet().remove(player);
        }
        Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
           channel.pipeline().remove(player.getName());
           return null;
        });
    }
}
