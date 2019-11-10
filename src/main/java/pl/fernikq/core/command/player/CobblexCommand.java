package pl.fernikq.core.command.player;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class CobblexCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CobblexCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        int cobblestoneInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.COBBLESTONE, (short) 0);
        if(cobblestoneInInventory < 576){
            return ChatUtil.sendMessage(player, MessagesManager.error("Potrzebujesz 576 bruku"));
        }
        ItemUtil.remove(new ItemStack(Material.COBBLESTONE, 1, (short) 0), player, 576);
        ItemUtil.giveItems(player, this.plugin.getDropManager().getCobblexItem().clone());
        ChatUtil.sendMessage(player, "&8>> {n}Utworzyles "+this.plugin.getDropManager().getCobblexItem().getItemMeta().getDisplayName());
        return true;
    }
}
