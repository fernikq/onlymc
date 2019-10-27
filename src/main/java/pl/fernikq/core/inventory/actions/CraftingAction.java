package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.CraftingActionType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class CraftingAction implements InventoryAction {

    private final CorePlugin plugin;
    private final Generator generator;
    private final CraftingActionType actionType;

    public CraftingAction(CorePlugin plugin, CraftingActionType actionType, Generator generator){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = generator;
    }

    public CraftingAction(CorePlugin plugin, CraftingActionType actionType){
        this.plugin = plugin;
        this.actionType = actionType;
        this.generator = null;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(actionType.equals(CraftingActionType.BACK)){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().craftingsMenu(user).openInventory(player);
            });
            return;
        }
        if(actionType.equals(CraftingActionType.CHOOSE)) {
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().craftings(user, generator).openInventory(player);
            });
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
