package pl.fernikq.core.command.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

public class HeadCommand extends CustomCommand {

    private final CorePlugin plugin;

    public HeadCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/head <nick>"));
        }
        ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(args[0]);
        ItemUtil.giveItems(player, head.toItemStack());
        ChatUtil.sendMessage(sender, "&8>> {n}Otrzymales glowe gracza {c}"+args[0]);
        return true;
    }
}
