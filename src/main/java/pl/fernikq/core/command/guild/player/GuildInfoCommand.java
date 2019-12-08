package pl.fernikq.core.command.guild.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildInfoCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildInfoCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g info <tag>"));
        }
        Player player = (Player) sender;
        if(args.length == 1){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
               if(!user.hasGuild()){
                   ChatUtil.sendMessage(sender, MessagesManager.usage("/g info <tag>"));
                   return;
               }
                this.plugin.getGuildManager().getGuildInfoMessages(user.getGuild()).forEach(s -> {
                    ChatUtil.sendMessage(player, s);
                });
            });
            return true;
        }
        this.plugin.getGuildManager().getGuildByTag(args[1]).peek(guild -> {
            this.plugin.getGuildManager().getGuildInfoMessages(guild).forEach(s -> {
                ChatUtil.sendMessage(player, s);
            });
        }).onEmpty(() -> ChatUtil.sendMessage(player, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
