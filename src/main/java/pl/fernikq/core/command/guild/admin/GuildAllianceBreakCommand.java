package pl.fernikq.core.command.guild.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildAllianceBreakCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildAllianceBreakCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga zerwij <tag> <tag>"));
        }
        String tag1 = args[1];
        String tag2 = args[2];
        this.plugin.getGuildManager().getGuildByTag(tag1).peek(guild1 -> {
            Guild guild2 = this.plugin.getGuildManager().getGuildByTag(tag2).getOrNull();
            if(guild2 == null){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!"));
                return;
            }
            if(guild1.equals(guild2)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podane gildie nie moga miec sojuszu!"));
                return;
            }
            if(!this.plugin.getAllianceManager().hasAlliance(guild1, guild2)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podane gildie nie maja sojuszu!"));
                return;
            }
            this.plugin.getAllianceManager().removeAlliance(guild1, guild2);
            ChatUtil.sendMessage(sender, "&8>> {n}Rozwiazales sojusz pomiedzy gildia &8[&f"+guild1.getTag()+"&8] {n}a gildia &8[&f"+guild2.getTag()+"&8]");
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
