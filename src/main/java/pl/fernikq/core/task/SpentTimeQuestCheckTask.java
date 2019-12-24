package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;

public class SpentTimeQuestCheckTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public SpentTimeQuestCheckTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(this.plugin, 1200, 1200);
    }

    @Override
    public void stop() {
        cancel();
    }

    @Override
    public void run(){
        Bukkit.getOnlinePlayers().forEach(o -> {
            this.plugin.getUserManager().getUser(o.getUniqueId()).peek(user -> {
               this.plugin.runAsync(() -> this.plugin.getQuestManager().checkTimeQuest(user));
            });
        });
    }
}
