package pl.fernikq.core.inventory.custom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.List;

public class CustomInventoryCommand extends CustomCommand {

    private final CustomInventory customInventory;

    public CustomInventoryCommand(String name, List<String> aliases, UserGroup group, CorePlugin plugin, CustomInventory customInventory){
        super(name, aliases, group, plugin);
        this.customInventory = customInventory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        this.customInventory.getInventoryGUI().openInventory(player);
        return true;
    }
}
