package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.CraftingsActionType;

public class CraftingsAction implements InventoryAction {

    private final CorePlugin plugin;
    private final Generator generator;
    private final CraftingsActionType actionType;

    public CraftingsAction(CorePlugin plugin, CraftingsActionType actionType, Generator generator){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = generator;
    }

    public CraftingsAction(CorePlugin plugin, CraftingsActionType actionType){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = null;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(actionType.equals(CraftingsActionType.BACK)){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().craftingsMenu(user).openInventory(player);
            });
            return;
        }
        if(actionType.equals(CraftingsActionType.CHOOSE)) {
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().craftings(user, generator).openInventory(player);
            });
        }
        if(actionType.equals(CraftingsActionType.CRAFT)) {
            //TODO
        }
    }
}
