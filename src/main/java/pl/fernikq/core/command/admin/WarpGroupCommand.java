package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class WarpGroupCommand extends CustomCommand {

    private final CorePlugin plugin;

    public WarpGroupCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/warpgroup <nazwa> <ranga>"));
        }
        String groupName = args[1];
        if(UserGroup.getByName(groupName) == null){
            ChatUtil.sendMessage(sender, MessagesManager.error("Podana ranga nie istnieje!"));
            return ChatUtil.sendMessage(sender, "&8>> {n}Dostepne rangi&8: "+UserGroup.getPrefixesToString());
        }
        this.plugin.getWarpManager().getWarp(args[0]).peek(warp -> {
            warp.setRequiredGroup(UserGroup.getByName(groupName));
            this.plugin.runAsync(() -> this.plugin.getWarpManager().getWarpData().updateWarp(warp));
            ChatUtil.sendMessage(sender, "&8>> {n}Zmieniles wymagana range warpu {c}"+warp.getName());
            return;
        }).onEmpty(() -> {
            ChatUtil.sendMessage(sender, MessagesManager.error("Podany warp nie istnieje!"));
            ChatUtil.sendMessage(sender, "&8>> {n}Dostpne warpy&8: "+this.plugin.getWarpManager().getWarpsToString());
            return;
        });
        return true;
    }
}
