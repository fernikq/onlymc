package pl.fernikq.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;

public class SidebarUpdateTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public SidebarUpdateTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getUserManager().getOnlineUsers().stream().filter(user -> user.getSidebar().isEnabled()).forEach(user -> {
            user.getSidebar().update();
        });
    }

    @Override
    public void start() {
        runTaskTimerAsynchronously(this.plugin, 60, 70);
    }

    @Override
    public void stop() {
        cancel();
    }
}
