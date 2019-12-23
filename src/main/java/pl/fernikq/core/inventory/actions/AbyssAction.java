package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.enums.AbyssActionType;
import pl.fernikq.core.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class AbyssAction implements InventoryAction {

    private int page;
    private CorePlugin plugin;
    private AbyssActionType type;

    public AbyssAction(CorePlugin plugin, AbyssActionType type, int page) {
        this.plugin = plugin;
        this.type = type;
        this.page = page;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        event.setCancelled(true);
        List<InventoryGUI> inventories = new ArrayList<>(this.plugin.getAbyssManager().getInventoriesToList());
        if(this.type.equals(AbyssActionType.NEXT)){
            if(this.page + 1 >= inventories.size()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz przewinac strony!"));
                return;
            }
            inventories.get(this.page + 1).openInventory(player);
        }
        if(this.type.equals(AbyssActionType.BACK)){
            if(this.page <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz juz cofnac strony!"));
                return;
            }
            inventories.get(this.page - 1).openInventory(player);
        }
    }
}
