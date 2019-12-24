package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.TopsActionType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;

public class TopsAction implements InventoryAction {

    private CorePlugin plugin;
    private Sortable top;
    private TopsActionType type;
    private User user;

    public TopsAction(CorePlugin plugin, Sortable top, TopsActionType type, User user){
        this.plugin = plugin;
        this.top = top;
        this.type = type;
        this.user = user;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(type.equals(TopsActionType.BACK_TO_MAIN_MENU)){
            this.plugin.getUserInventory().topsMenu(user).openInventory(player);
            return;
        }
        if(type.equals(TopsActionType.OPEN_USER_TOPS_SELECT)){
            this.plugin.getUserInventory().playerTops(user).openInventory(player);
            return;
        }
        if(type.equals(TopsActionType.CHOOSE_USER_TOP)){
            this.plugin.getUserInventory().playerTop(user, top).openInventory(player);
            return;
        }
        if(type.equals(TopsActionType.OPEN_GUILD_TOPS_SELECT)){
            this.plugin.getGuildInventory().guildTops(user).openInventory(player);
            return;
        }
        if(type.equals(TopsActionType.CHOOSE_GUILD_TOP)){
            this.plugin.getGuildInventory().guildTop(user, top).openInventory(player);
            return;
        }
    }
}
