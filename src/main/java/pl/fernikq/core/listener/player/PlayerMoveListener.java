package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.PlayerUtil;

public class PlayerMoveListener implements Listener {

    private final CorePlugin plugin;

    public PlayerMoveListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(LocationUtil.moveXZ(event.getFrom(), event.getTo())) {
            Guild guildFrom = this.plugin.getGuildManager().getGuildByLocation(event.getFrom().getBlock().getLocation()).getOrNull();
            Guild guildTo = this.plugin.getGuildManager().getGuildByLocation(event.getTo().getBlock().getLocation()).getOrNull();
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            if(user.getUserFight().isDuringFight()){
                RegionFeedback regionFeedback = this.plugin.getRegionManager().canJoinDuringPVP(user, event.getTo(), event.getFrom());
                if(!regionFeedback.isPermit()){
                    player.teleport(event.getFrom());
                    PlayerUtil.punchPlayer(player, event.getTo(), event.getFrom());
                    return;
                }
            }
            if(guildFrom != null && guildTo == null){
                ChatUtil.sendMessage(player, MessagesManager.guildQuitCuboidMessage.replace("{TAG}", guildFrom.getTag()));
                return;
            }
            if(guildTo != null && guildFrom == null){
                ChatUtil.sendMessage(player, MessagesManager.guildJoinCuboidMessage.replace("{TAG}", guildTo.getTag()));
                if(user != null){
                    if((user.hasGuild() && user.getGuild().equals(guildTo)) || (user.hasGuild() && this.plugin.getAllianceManager().hasAlliance(user.getGuild(), guildTo))){
                        return;
                    }
                    guildTo.getOnlineMembers().stream().forEach(member -> {
                        ChatUtil.sendMessage(member.getUser().asPlayer(), MessagesManager.guildIntruderMessage.replace("{PLAYER}", player.getName()));
                    });
                }
                return;
            }
        }
    }
}
