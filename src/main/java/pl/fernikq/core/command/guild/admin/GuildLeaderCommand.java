package pl.fernikq.core.command.guild.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildLeaderCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildLeaderCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga lider <nick>"));
        }
        String name = args[1];
        this.plugin.getUserManager().getUser(name).peek(user -> {
            if(!user.hasGuild()) {
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nie posiada gildii!"));
                return;
            }
            Guild guild = user.getGuild();
            guild.setOwner(user);
            ChatUtil.sendMessage(sender, "&8>> {n}Gracz {c}"+user.getName()+" {n}zostal nowym liderem gildii &8[&f"+guild.getTag()+"&8]");
        }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
        return true;
    }
}
