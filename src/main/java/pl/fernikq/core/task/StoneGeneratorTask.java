package pl.fernikq.core.task;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.util.TimeUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StoneGeneratorTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public StoneGeneratorTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(this.plugin, 40, 5);
    }

    @Override
    public void stop() {
        cancel();
    }

    @Override
    public void run(){
        this.plugin.getStoneGeneratorManager().getGeneratorsToRegen().stream()
                .filter(generator -> generator.getRegenerationTime() < System.currentTimeMillis())
                .forEach(generator -> {
            generator.getLocation().getBlock().setType(Material.STONE);
            this.plugin.getStoneGeneratorManager().successfulRegen(generator);
        });
    }
}
