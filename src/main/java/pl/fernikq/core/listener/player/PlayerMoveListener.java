package pl.fernikq.core.listener.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.PlayerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerMoveListener implements Listener {

    private final CorePlugin plugin;
    private final Map<UUID, Location> firstPlayerLocation = new HashMap<>();

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
            if(user == null){
                return;
            }
            if(ConfigManager.freeze && !user.canByGroup(UserGroup.TEST_HELPER)){
                if(!this.firstPlayerLocation.containsKey(player.getUniqueId())){
                    this.firstPlayerLocation.put(player.getUniqueId(), player.getLocation());
                }
                Location location = this.firstPlayerLocation.get(player.getUniqueId());
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                player.teleport(location);
            }else{
                if(this.firstPlayerLocation.containsKey(player.getUniqueId())){
                    this.firstPlayerLocation.remove(player.getUniqueId());
                }
            }
            user.getUserStat().setDistanceTraveled(user.getUserStat().getDistanceTraveled() + 1);
            if(player.getLocation().getBlockY() <= 0){
                player.teleport(LocationUtil.locationFromString(ConfigManager.spawnLocation));
                ChatUtil.sendMessage(player, "Siwy dym");
            }
            this.plugin.getQuestManager().checkQuest(user, QuestType.TRAVELED_DISTANCE);
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canMoveCauseOfBorder(event.getTo(), event.getFrom());
            if(!regionFeedback.isPermit()){
                player.teleport(event.getFrom());
                PlayerUtil.punchPlayer(player, event.getTo(), event.getFrom());
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
                return;
            }
            if(user.getUserFight().isDuringFight()){
                regionFeedback = this.plugin.getRegionManager().canJoinDuringPVP(user, event.getTo().getBlock().getLocation(), event.getFrom().getBlock().getLocation());
                if(!regionFeedback.isPermit()){
                    player.teleport(event.getFrom());
                    PlayerUtil.punchPlayer(player, event.getTo(), event.getFrom());
                    ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
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
                    if(user.hasGuild()){
                        if(!user.getGuild().equals(guildTo)) {
                            user.getUserStat().getExploredGuilds().add(guildTo.getTag());
                            this.plugin.getQuestManager().checkQuest(user, QuestType.EXPLORE_GUILDS);
                        }
                    }else{
                        user.getUserStat().getExploredGuilds().add(guildTo.getTag());
                        this.plugin.getQuestManager().checkQuest(user, QuestType.EXPLORE_GUILDS);
                    }
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
