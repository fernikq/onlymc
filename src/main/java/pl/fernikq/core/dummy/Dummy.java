package pl.fernikq.core.dummy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
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
            objective.getScore(user.getName()).setScore(user.getUserStat().getPoints());
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
                finalObjective.getScore(onlineUser.getName()).setScore(onlineUser.getUserStat().getPoints());
            });
        });
    }
}
