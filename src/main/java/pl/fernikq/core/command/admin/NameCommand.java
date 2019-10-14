package pl.fernikq.core.command.admin;

import org.apache.commons.lang.StringUtils;
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

public class NameCommand extends CustomCommand {

    private final CorePlugin plugin;

    public NameCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/name <nazwa>"));
        }
        String name = StringUtils.join(args, " ", 0, args.length);
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany przedmiot nie moze zostac nazwany!"));
        }
        new ItemBuilder(player.getItemInHand()).setName(ChatUtil.fixColor(name));
        return ChatUtil.sendMessage(sender, "&8>> &aPomyslnie {n}nazwales przedmiot!");
    }
}
