package pl.fernikq.core.dummy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import pl.fernikq.core.CoreAPI;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.incognito.UserIncognito;
import pl.fernikq.core.util.ChatUtil;

public class Dummy {

    private User user;

    public Dummy(User user){
        this.user = user;
    }

    public void updateScore(User user, CorePlugin plugin){
        if(this.user.asPlayer() == null || this.user.getScoreboard() == null){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        Objective objective = scoreboard.getObjective("CORE-POINTS");
        if(objective == null){
            createScore(plugin);
        }else{
            int score = plugin.getIncognitoManager().changePoints(user, this.user) ? 000 : user.getUserStat().getPoints();
            objective.getScore(user.getName()).setScore(score);
        }
    }

    public void createScore(CorePlugin plugin){
        if(this.user.asPlayer() == null || this.user.getScoreboard() == null){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        Objective objective = scoreboard.getObjective("CORE-POINTS");
        if(objective == null){
            objective = scoreboard.registerNewObjective("CORE-POINTS", "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatUtil.fixColor(MessagesManager.pointsBelowNameSuffix));
        }
        Objective finalObjective = objective;
        Bukkit.getOnlinePlayers().forEach(online -> {
            plugin.getUserManager().getUser(online.getUniqueId()).peek(onlineUser -> {
                int score = plugin.getIncognitoManager().changePoints(onlineUser, this.user) ? 000 : onlineUser.getUserStat().getPoints();
                finalObjective.getScore(onlineUser.getName()).setScore(score);
            });
        });
    }
}
