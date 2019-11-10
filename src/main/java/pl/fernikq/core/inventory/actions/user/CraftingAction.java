package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.CraftingActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class CraftingAction implements InventoryAction {

    private final CorePlugin plugin;
    private final Generator generator;
    private final CraftingActionType actionType;
    private final User user;

    public CraftingAction(CorePlugin plugin, CraftingActionType actionType, Generator generator, User user){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = generator;
        this.user = user;
    }

    public CraftingAction(CorePlugin plugin, CraftingActionType actionType, User user){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = null;
        this.user = user;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(actionType.equals(CraftingActionType.BACK)){
            this.plugin.getUserInventory().craftingsMenu(user).openInventory(player);
            return;
        }
        if(actionType.equals(CraftingActionType.CHOOSE)) {
            this.plugin.getUserInventory().craftings(user, generator).openInventory(player);
            return;
        }
        if(actionType.equals(CraftingActionType.CRAFT)) {
            if(!generator.hasItems(player)) {
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz potrzebnych przedmiotow!"));
                return;
            }
            generator.removeItems(player);
            ChatUtil.sendMessage(player, "&8>> {n}Utworzyles {c}" + (generator.getItemStack().getItemMeta().getDisplayName() == null ? generator.getItemStack().getType().name().toLowerCase() : generator.getItemStack().getItemMeta().getDisplayName()));
            ItemUtil.giveItems(player, generator.getItemStack().clone());
            this.plugin.getUserInventory().craftings(this.plugin.getUserManager().getUser(player.getUniqueId()).get(), generator).openInventory(player);
        }
    }
}
