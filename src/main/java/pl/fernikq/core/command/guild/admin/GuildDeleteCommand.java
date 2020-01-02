package pl.fernikq.core.command.guild.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildDeleteCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildDeleteCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga usun <tag>"));
        }
        String tag = args[1].toUpperCase();
        this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
            this.plugin.getGuildManager().deleteGuild(guild);
            Bukkit.getOnlinePlayers().forEach(o -> {
                ChatUtil.sendMessage(o, "&8>> {n}Gildia &8[&f"+guild.getTag()+"&8] {n}zostala usunieta przez administratora {c}"+sender.getName());
            });
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
