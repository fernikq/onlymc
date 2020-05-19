package pl.fernikq.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.comparator.Sortable;

public class TopsSortTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public TopsSortTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimerAsynchronously(this.plugin, 100, 100);
    }

    @Override
    public void stop() {
        cancel();
    }


    @Override
    public void run() {
        this.plugin.getTopManager().getTopsByKind(TopKind.GUILD).stream().filter(sortable -> !sortable.isSorted()).forEach(sortable -> {
            sortable.setSorted(true);
            sortable.sort();
        });
        this.plugin.getTopManager().getTopsByKind(TopKind.USER).stream().filter(sortable -> !sortable.isSorted()).forEach(sortable -> {
            sortable.setSorted(true);
            sortable.sort();
        });
    }
}
