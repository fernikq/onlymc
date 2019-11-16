package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;

public class RemoveItemsTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;
    private int amount;

    public RemoveItemsTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 20, 3600);
    }

    @Override
    public void stop() {
        cancel();
    }


    @Override
    public void run() {
        amount = 0;
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).filter(entity -> entity.getTicksLived() >= 1200).forEach(entity -> {
                entity.remove();
                amount += ((Item)entity).getItemStack().getAmount();
            });
        });
        Bukkit.getOnlinePlayers().forEach(online -> {
            ChatUtil.sendMessage(online, "&8>> {n}Usunieto {c}"+amount+" {n}"+getCorrectName(amount)+" z ziemi&8!");
        });
    }

    public String getCorrectName(int amount){
        if(amount == 0){
            return "przedmiotow";
        }
        if(amount == 1){
            return "przedmiot";
        }
        if(amount < 5){
            return "przedmioty";
        }
        return "przedmiotow";
    }
}
