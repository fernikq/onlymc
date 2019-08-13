package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GroupCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GroupCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/group <nick> <ranga>"));
        }
        UserGroup group = UserGroup.getByName(args[1]);
        if(group == null){
            return ChatUtil.sendMessage(sender, "&8>> {n}Dostepne rangi&8: "+UserGroup.getPrefixesToString());
        }
        this.plugin.getUserManager().getUser(args[0])
                .onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists))
                .peek(user -> {
                    ChatUtil.sendMessage(sender, "&8>> {n}Nadales range {c}"+group.name()+" &fgraczowi {c}"+user.getName());
                    user.setGroup(group);
                    if(user.isOnline()){
                        this.plugin.getTagManager().updateTag(user.asPlayer());
                    }
                });
        return false;
    }
}
