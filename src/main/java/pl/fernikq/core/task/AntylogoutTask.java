package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TitleUtil;

public class AntylogoutTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public AntylogoutTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(o -> {
            this.plugin.getUserManager().getUser(o.getUniqueId()).peek(user -> {
                if(user.getUserFight().getLastAttackTime() == 0L){
                    return;
                }
                if(!user.getUserFight().isDuringFight()){
                    if(user.getUserFight().wasDuringFight(3)) {
                        TitleUtil.sendActionBar(o, ChatUtil.fixColor(MessagesManager.playerFightFinishMessage));
                        return;
                    }
                    if(!user.getUserFight().wasDuringFight(30)){
                        user.getUserFight().removeFight();
                    }
                }
                if(user.getUserFight().isDuringFight()){
                    TitleUtil.sendActionBar(o, ChatUtil.fixColor(MessagesManager.playerFightAntylogoutMessage.replace("{TIME}", Integer.toString(user.getUserFight().getTimeToEnd()))));
                    return;
                }
            });
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
