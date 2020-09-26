package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.CraftingActionType;
import pl.fernikq.core.inventory.enums.user.MagicCaseActionType;
import pl.fernikq.core.magiccase.MagicCaseType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class MagicCaseAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private MagicCaseActionType magicCaseActionType;
    private MagicCaseType magicCaseType;

    public MagicCaseAction(CorePlugin plugin, MagicCaseActionType magicCaseActionType, MagicCaseType magicCaseType, User user){
        this.plugin = plugin;
        this.user = user;
        this.magicCaseActionType = magicCaseActionType;
        this.magicCaseType = magicCaseType;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(this.magicCaseActionType == MagicCaseActionType.BACK_TO_MENU){
            this.plugin.getUserInventory().magicCaseMenu(this.user).openInventory(player);
            return;
        }
        if(this.magicCaseActionType == MagicCaseActionType.OPEN_DROP){
            this.plugin.getUserInventory().magicCaseDrop(this.user, this.magicCaseType).openInventory(player);
            return;
        }
        if(this.magicCaseActionType == MagicCaseActionType.CHANGE_FRAGMENTS){
            if(this.user.getUserStat().getKeyFragmentsByMagicCaseType(this.magicCaseType) < this.plugin.getMagicCaseManager().getFragmentsRequiredByMagicCaseType(this.magicCaseType)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci fragmentow klucza!"));
                return;
            }
            this.user.getUserStat().removeKeyFragmentsByMagicCaseType(this.magicCaseType, this.plugin.getMagicCaseManager().getFragmentsRequiredByMagicCaseType(this.magicCaseType));
            ItemUtil.giveItems(player, this.plugin.getMagicCaseManager().getKeyByMagicCaseType(this.magicCaseType));
            ChatUtil.sendMessage(player, "&8>> &aPomyslnie &fwymieniles fragmenty klucza na klucz do skrzyni o typie&8: "+this.magicCaseType.getName());
            this.plugin.getUserInventory().magicCaseMenu(this.user).openInventory(player);
            return;
        }
    }
}
