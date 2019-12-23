package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class RemoveItemsTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;
    private int amount;

    public RemoveItemsTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 3600, 3600);
    }

    @Override
    public void stop() {
        cancel();
    }


    @Override
    public void run() {
        amount = 0;
        List<ItemStack> itemStackList = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).filter(entity -> entity.getTicksLived() >= 1200).forEach(entity -> {
                entity.remove();
                amount += ((Item)entity).getItemStack().getAmount();
                itemStackList.add(((Item) entity).getItemStack());
            });
        });
        Bukkit.getOnlinePlayers().forEach(online -> {
            ChatUtil.sendMessage(online, "&8>> {n}Usunieto {c}"+amount+" {n}"+getCorrectName(amount)+" z ziemi&8!");
            ChatUtil.sendMessage(online, "&8>> {n}Mozesz je sprawdzic pod {c}/otchlan!");
        });
        this.plugin.getAbyssManager().createInventories(itemStackList);
        new BukkitRunnable(){
            int time = 0;
            @Override
            public void run() {
                if(time >= 30){
                    Bukkit.getOnlinePlayers().forEach(o -> {
                        ChatUtil.sendMessage(o, "&8>> {n}Otchlan zostala zamknieta!");
                        if(o.getOpenInventory() != null){
                            plugin.getAbyssManager().getInventoriesToList().forEach(inventoryGUI -> {
                                if(inventoryGUI.getInventory().equals(o.getOpenInventory().getTopInventory())){
                                    o.closeInventory();
                                    return;
                                }
                            });
                        }
                    });
                    plugin.getAbyssManager().setOpened(false);
                    plugin.getAbyssManager().getInventories().clear();
                    cancel();
                }
                if(time == 27){
                    Bukkit.getOnlinePlayers().forEach(o -> ChatUtil.sendMessage(o, "&8>> {n}Otchlan zostanie zamknieta za {c}3 {n}sekundy!"));
                }
                if(time == 28){
                    Bukkit.getOnlinePlayers().forEach(o -> ChatUtil.sendMessage(o, "&8>> {n}Otchlan zostanie zamknieta za {c}2 {n}sekundy!"));
                }
                if(time == 29){
                    Bukkit.getOnlinePlayers().forEach(o -> ChatUtil.sendMessage(o, "&8>> {n}Otchlan zostanie zamknieta za {c}1 {n}sekunde!"));
                }
                time++;
            }
        }.runTaskTimer(this.plugin, 0, 20);
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
