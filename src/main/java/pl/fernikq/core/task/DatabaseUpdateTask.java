package pl.fernikq.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.top.TopKind;

import java.util.HashSet;
import java.util.Set;

public class DatabaseUpdateTask extends BukkitRunnable implements SimpleTask  {

    private final CorePlugin plugin;

    public DatabaseUpdateTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimerAsynchronously(this.plugin, 6000, 6000);
    }

    @Override
    public void stop() {
        cancel();
    }


    @Override
    public void run() {
       Set<Guild> guilds = new HashSet<>();
       this.plugin.getUserManager().getOnlineUsers().forEach(user -> {
           user.getUserStat().setSpentTime(user.getUserStat().getSpentTime() + (System.currentTimeMillis() - user.getUserStat().getJoinTime()));
           user.getUserStat().setJoinTime(System.currentTimeMillis());
           this.plugin.getUserManager().updateUser(user);
           if(user.hasGuild()) guilds.add(user.getGuild());
       });
       guilds.forEach(guild -> this.plugin.getGuildManager().updateGuild(guild));
    }
}
