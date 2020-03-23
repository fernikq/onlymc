package pl.fernikq.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;

public class AlwaysDayTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public AlwaysDayTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getServer().getWorlds().forEach(world -> world.setTime(0));
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 1200, 1200);
    }

    @Override
    public void stop() {
        cancel();
    }
}
