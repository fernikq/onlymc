package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.ChatSettingsActionType;
import pl.fernikq.core.user.User;

public class ChatSettingsAction implements InventoryAction {

    private User user;
    private CorePlugin plugin;
    private ChatSettingsActionType type;

    public ChatSettingsAction(CorePlugin plugin, ChatSettingsActionType type, User user){
        this.user = user;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(type.equals(ChatSettingsActionType.AUTOMESSAGE)){
            user.getUserChat().setAutoMessages(!user.getUserChat().isAutoMessages());
            this.plugin.getUserInventory().chatSettings(user).openInventory(player);
            return;
        }
        if(type.equals(ChatSettingsActionType.FIGHT)){
            user.getUserChat().setFightMessages(!user.getUserChat().isFightMessages());
            this.plugin.getUserInventory().chatSettings(user).openInventory(player);
            return;
        }
        if(type.equals(ChatSettingsActionType.GUILD)){
            user.getUserChat().setGuildMessages(!user.getUserChat().isGuildMessages());
            this.plugin.getUserInventory().chatSettings(user).openInventory(player);
            return;
        }
        if(type.equals(ChatSettingsActionType.CASE)){
            user.getUserChat().setPremiumCaseMessages(!user.getUserChat().isPremiumCaseMessages());
            this.plugin.getUserInventory().chatSettings(user).openInventory(player);
            return;
        }
    }
}
