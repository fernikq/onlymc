package pl.fernikq.core.command.guild.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TeleportManager;

public class GuildTeleportCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildTeleportCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga tp <tag>"));
        }
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        String tag = args[1].toUpperCase();
        this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
            this.plugin.getTeleportManager().teleportToLocation(player, guild.getRegion().getHome(), 0, "baze gildii &8[&f"+guild.getTag()+"&8]");
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
