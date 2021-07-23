package pl.fernikq.core.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.StringUtil;

public class DepositeTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public DepositeTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(this.plugin, 20*5, 20*5);
    }

    @Override
    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(online -> {
            this.plugin.getUserManager().getUser(online.getUniqueId()).peek(user -> {
                int apples = ItemUtil.getAmountOfMaterial(online.getInventory(), Material.GOLDEN_APPLE, (short) 0);
                int enchantedApples = ItemUtil.getAmountOfMaterial(online.getInventory(), Material.GOLDEN_APPLE, (short) 1);
                int pearls = ItemUtil.getAmountOfMaterial(online.getInventory(), Material.ENDER_PEARL);
                int arrows = ItemUtil.getAmountOfMaterial(online.getInventory(), Material.ARROW);
                int snowballs = ItemUtil.getAmountOfMaterial(online.getInventory(), Material.SNOW_BALL);
                if(apples > ConfigManager.maxGoldenApplesInInventory){
                    int toRemove = apples - ConfigManager.maxGoldenApplesInInventory;
                    ItemUtil.remove(new ItemStack(Material.GOLDEN_APPLE), online, toRemove);
                    user.getUserStat().addDepositeApples(toRemove);
                    String message = StringUtil.replace(MessagesManager.depositeApplesMessage, "{AMOUNT}", toRemove);
                    ChatUtil.sendMessage(online, message);
                }
                if(enchantedApples > ConfigManager.maxEnchantedGoldenApplesInInventory){
                    int toRemove = enchantedApples - ConfigManager.maxEnchantedGoldenApplesInInventory;
                    ItemUtil.remove(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), online, toRemove);
                    user.getUserStat().addDepositeEnchantedApples(toRemove);
                    String message = StringUtil.replace(MessagesManager.depositeEnchantedApplesMessage, "{AMOUNT}", toRemove);
                    ChatUtil.sendMessage(online, message);
                }
                if(pearls > ConfigManager.maxPearlsInInventory){
                    int toRemove = pearls - ConfigManager.maxPearlsInInventory;
                    ItemUtil.remove(new ItemStack(Material.ENDER_PEARL), online, toRemove);
                    user.getUserStat().addDepositePearls(toRemove);
                    String message = StringUtil.replace(MessagesManager.depositePearlsMessage, "{AMOUNT}", toRemove);
                    ChatUtil.sendMessage(online, message);
                }
                if(arrows > ConfigManager.maxArrowsInInventory){
                    int toRemove = arrows - ConfigManager.maxArrowsInInventory;
                    ItemUtil.remove(new ItemStack(Material.ARROW), online, toRemove);
                    user.getUserStat().setDepositeArrows(user.getUserStat().getDepositeArrows() + toRemove);
                    String message = StringUtil.replace(MessagesManager.depositeArrowsMessage, "{AMOUNT}", toRemove);
                    ChatUtil.sendMessage(online, message);
                }
                if(snowballs > ConfigManager.maxSnowballsInInventory){
                    int toRemove = snowballs - ConfigManager.maxSnowballsInInventory;
                    ItemUtil.remove(new ItemStack(Material.SNOW_BALL), online, toRemove);
                    user.getUserStat().setDepositeSnowballs(user.getUserStat().getDepositeSnowballs() + toRemove);
                    String message = StringUtil.replace(MessagesManager.depositeSnowballsMessage, "{AMOUNT}", toRemove);
                    ChatUtil.sendMessage(online, message);
                }
            });
        });
    }
}
