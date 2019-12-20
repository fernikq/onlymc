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
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
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
        User victimUser = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(victimUser == null){
            return;
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
            victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - points);
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
            Bukkit.getOnlinePlayers().forEach(o -> {
                ChatUtil.sendMessage(o, finalMessage);
                ChatUtil.sendMessage(o, finalAssistMessage);
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
            Bukkit.getOnlinePlayers().forEach(o -> {
                ChatUtil.sendMessage(o, finalMessage);
            });
        }
        this.plugin.getFightManager().removeFight(victimUser);
    }
}
