package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.drop.Drop;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.DropActionType;
import pl.fernikq.core.user.User;

public class DropAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final Drop drop;
    private final DropActionType dropActionType;

    public DropAction(CorePlugin plugin, DropActionType dropActionType, User user){
        this.plugin = plugin;
        this.user = user;
        this.drop = null;
        this.dropActionType = dropActionType;
    }

    public DropAction(CorePlugin plugin, Drop drop, DropActionType dropActionType, User user){
        this.plugin = plugin;
        this.user = user;
        this.drop = drop;
        this.dropActionType = dropActionType;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(dropActionType.equals(DropActionType.BACK_TO_MENU)){
            this.plugin.getUserInventory().dropMenu(this.user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OPEN_STONE_DROP)){
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OPEN_PREMIUMCASE_DROP)){
            this.plugin.getUserInventory().dropPremiumCase(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OPEN_LEVEL_SHOP)){
            this.plugin.getUserInventory().levelShopBuy(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OPEN_COBBLEX_DROP)){
            this.plugin.getUserInventory().dropCobblex(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OPEN_STATISTICS)){
            this.plugin.getUserInventory().dropStatistics(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.CHANGE_ONE_DROP)){
            this.drop.changeDropStatus(user);
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.OFF_ALL_DROPS)){
            for(Drop drop : this.plugin.getDropManager().getDrops(DropType.STONE)){
                drop.addToDisabled(user);
            }
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.ON_ALL_DROPS)){
            for(Drop drop : this.plugin.getDropManager().getDrops(DropType.STONE)){
                drop.removeFromDisabled(user);
            }
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.CHANGE_COBBLESTONE_STATUS)){
            this.plugin.getDropManager().changeDropStatus(user);
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
        if(dropActionType.equals(DropActionType.CHANGE_MESSAGES_STATUS)){
            this.user.getUserChat().setDropMessages(!this.user.getUserChat().isDropMessages());
            this.plugin.getUserInventory().dropStone(user).openInventory(player);
            return;
        }
    }
}
