package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TitleUtil;

import java.util.HashSet;

public class AntylogoutTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public AntylogoutTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        new HashSet<>(this.plugin.getFightManager().getUsersDuringFight()).forEach(user -> {
            if(!user.getUserFight().isDuringFight()) {
                if(user.getUserFight().wasDuringFight(5)) {
                    TitleUtil.sendActionBar(user.asPlayer(), ChatUtil.fixColor(MessagesManager.playerFightFinishMessage));
                    return;
                }
                if(!user.getUserFight().wasDuringFight(30)) {
                    this.plugin.getFightManager().removeFight(user);
                    return;
                }
            }else {
                TitleUtil.sendActionBar(user.asPlayer(), ChatUtil.fixColor(MessagesManager.playerFightAntylogoutMessage.replace("{TIME}", Integer.toString(user.getUserFight().getTimeToEnd()))));
            }
        });
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void stop() {
        cancel();
    }
}
