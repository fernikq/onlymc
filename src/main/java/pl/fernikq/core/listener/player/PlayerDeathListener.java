package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.RankingUtil;

import java.util.List;

public class PlayerDeathListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDeathListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event){
        event.setDeathMessage(null);
        Player player = event.getEntity();
        if(player.isOnline() && player.isDead()){
            player.spigot().respawn();
        }
        User victimUser = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(victimUser == null){
            return;
        }
        victimUser.getUserStat().setDeaths(victimUser.getUserStat().getDeaths() + 1);
        this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_DEATHS).sort());
        if(victimUser.hasGuild()){
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_DEATHS).sort());
        }
        UserFight victimFight = victimUser.getUserFight();
        if(victimFight.getDamageMap().isEmpty() && victimFight.getLastAttacker() == null){
            return;
        }
        List<Damage> damageList = victimFight.getDamageList();
        User killerUser = victimFight.getLastAttacker();
        Damage killerDamage = victimFight.getDamageByUser(killerUser);
        damageList.remove(killerDamage);
        if(!damageList.isEmpty()){
            Damage assistDamage = damageList.get(0);
            User assistUser = assistDamage.getUser();
            int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
            int maxAssistPoints = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), assistUser.getUserStat().getPoints());
            int assistPoints = points * 25/100;
            if(assistPoints > maxAssistPoints){
                assistPoints = maxAssistPoints;
            }
            points = points - assistPoints;
            if(points < 0){
                points = 0;
            }
            if(assistPoints < 0){
                assistPoints = 0;
            }
            killerUser.getUserStat().setPoints(killerUser.getUserStat().getPoints() + points);
            assistUser.getUserStat().setPoints(assistUser.getUserStat().getPoints() + assistPoints);
            assistUser.getUserStat().setAssists(assistUser.getUserStat().getAssists() + 1);
            this.plugin.runAsync(() -> this.plugin.getQuestManager().checkQuest(assistUser, QuestType.ASSISTS));
            victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - points);
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_ASSISTS).sort());
            if(assistUser.hasGuild()){
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_ASSISTS).sort());
            }
            this.plugin.getDummyManager().updateScore(assistUser);
            this.plugin.getDummyManager().updateScore(victimUser);
            this.plugin.getDummyManager().updateScore(killerUser);
            String message = MessagesManager.playerFightMessage;
            String assistMessage = MessagesManager.playerFightPlusAssistMessage;
            message = message.replace("{KILLER}", killerUser.getName());
            message = message.replace("{KILLER-GUILD}", killerUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", killerUser.getGuild().getTag()) : "");
            message = message.replace("{KILLER-POINTS}", Integer.toString(points));
            message = message.replace("{VICTIM}", victimUser.getName());
            message = message.replace("{VICTIM-GUILD}", victimUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", victimUser.getGuild().getTag()) : "");
            message = message.replace("{VICTIM-POINTS}", Integer.toString(points));
            assistMessage = assistMessage.replace("{ASSIST}", assistUser.getName());
            assistMessage = assistMessage.replace("{ASSIST-GUILD}", assistUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", assistUser.getGuild().getTag()) : "");
            assistMessage = assistMessage.replace("{ASSIST-POINTS}", Integer.toString(assistPoints));
            String finalMessage = message;
            String finalAssistMessage = assistMessage;
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isFightMessages()).forEach(onlineUser -> {
                ChatUtil.sendMessage(onlineUser.asPlayer(), finalMessage);
                ChatUtil.sendMessage(onlineUser.asPlayer(), finalAssistMessage);
            });
        }else{
            int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
            killerUser.getUserStat().setPoints(killerUser.getUserStat().getPoints() + points);
            victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - points);
            this.plugin.getDummyManager().updateScore(victimUser);
            this.plugin.getDummyManager().updateScore(killerUser);
            String message = MessagesManager.playerFightMessage;
            message = message.replace("{KILLER}", killerUser.getName());
            message = message.replace("{KILLER-GUILD}", killerUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", killerUser.getGuild().getTag()) : "");
            message = message.replace("{KILLER-POINTS}", Integer.toString(points));
            message = message.replace("{VICTIM}", victimUser.getName());
            message = message.replace("{VICTIM-GUILD}", victimUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", victimUser.getGuild().getTag()) : "");
            message = message.replace("{VICTIM-POINTS}", Integer.toString(points));
            String finalMessage = message;
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isFightMessages()).forEach(onlineUser -> {
                ChatUtil.sendMessage(onlineUser.asPlayer(), finalMessage);
            });
        }
        if(victimUser.canByGroup(UserGroup.VIP)){
            killerUser.getUserStat().getKilledWithRankUsers().add(victimUser);
            this.plugin.runAsync(() -> this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_USERS_WITH_RANK));
        }
        killerUser.getUserStat().getKilledUsers().add(victimUser);
        killerUser.getUserStat().setKills(killerUser.getUserStat().getKills() + 1);
        this.plugin.runAsync(() -> {
            this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_USER);
            this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_UNIQUE_USERS);
            this.plugin.getTopManager().getTopByType(TopType.USER_POINTS).sort();
            this.plugin.getTopManager().getTopByType(TopType.USER_KILLS).sort();
        });
        if(killerUser.hasGuild() || victimUser.hasGuild()){
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_POINTS).sort());
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_KILLS).sort());
        }
        this.plugin.getFightManager().removeFight(victimUser);
    }
}
