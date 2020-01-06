package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class EnderchestUpgradeAction implements InventoryAction {

    private int level;
    private User user;
    private CorePlugin plugin;

    public EnderchestUpgradeAction(CorePlugin plugin, int level, User user){
        this.plugin = plugin;
        this.level = level;
        this.user = user;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(this.user.getEnderchest().getLevel() >= 3){
            ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz maksymalny poziom enderchesta!"));
            return;
        }
        if(this.level == 1){
            if(this.user.getEnderchest().getLevel() == this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz juz ten poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() > this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz wyzszy poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() != this.level - 1){
                ChatUtil.sendMessage(player, "&8>> {n}Aby ulepszyc enderchest na poziom {c}"+this.level+" {n}musisz posiadac poziom {c}"+(this.level-1));
                return;
            }
            if(this.user.getUserStat().getCoins() < this.user.getEnderchest().getCostByLevel()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
                return;
            }
            this.user.getUserStat().removeCoins(this.user.getEnderchest().getCostByLevel());
            this.user.getEnderchest().setLevel(this.level);
            this.user.getEnderchest().recalculateEnderchest();
            ChatUtil.sendMessage(player, "&8>> {n}Ulepszyles enderchest do poziomu {c}"+this.level);
            this.plugin.getUserInventory().enderchestUpgrade(this.user).openInventory(player);
            user.getSidebar().update();
            return;
        }
        if(this.level == 2){
            if(this.user.getEnderchest().getLevel() == this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz juz ten poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() > this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz wyzszy poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() != this.level - 1){
                ChatUtil.sendMessage(player, "&8>> {n}Aby ulepszyc enderchest na poziom {c}"+this.level+" {n}musisz posiadac poziom {c}"+(this.level-1));
                return;
            }
            if(this.user.getUserStat().getCoins() < this.user.getEnderchest().getCostByLevel()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
                return;
            }
            this.user.getUserStat().removeCoins(this.user.getEnderchest().getCostByLevel());
            this.user.getEnderchest().setLevel(this.level);
            this.user.getEnderchest().recalculateEnderchest();
            ChatUtil.sendMessage(player, "&8>> {n}Ulepszyles enderchest do poziomu {c}"+this.level);
            this.plugin.getUserInventory().enderchestUpgrade(this.user).openInventory(player);
            user.getSidebar().update();
            return;
        }
        if(this.level == 3){
            if(this.user.getEnderchest().getLevel() == this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz juz ten poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() > this.level){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz wyzszy poziom enderchesta!"));
                return;
            }
            if(this.user.getEnderchest().getLevel() != this.level - 1){
                ChatUtil.sendMessage(player, "&8>> {n}Aby ulepszyc enderchest na poziom {c}"+this.level+" {n}musisz posiadac poziom {c}"+(this.level-1));
                return;
            }
            if(this.user.getUserStat().getCoins() < this.user.getEnderchest().getCostByLevel()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
                return;
            }
            this.user.getUserStat().removeCoins(this.user.getEnderchest().getCostByLevel());
            this.user.getEnderchest().setLevel(this.level);
            this.user.getEnderchest().recalculateEnderchest();
            ChatUtil.sendMessage(player, "&8>> {n}Ulepszyles enderchest do poziomu {c}"+this.level);
            this.plugin.getUserInventory().enderchestUpgrade(this.user).openInventory(player);
            user.getSidebar().update();
            return;
        }
    }
}
