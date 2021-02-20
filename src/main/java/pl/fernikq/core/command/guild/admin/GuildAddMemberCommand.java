package pl.fernikq.core.command.guild.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TitleUtil;

public class GuildAddMemberCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildAddMemberCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga dodaj <tag> <nick>"));
        }
        String tag = args[1].toUpperCase();
        String name = args[2];
        this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
            this.plugin.getUserManager().getUser(name).peek(user -> {
                if(user.hasGuild()) {
                    ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz posiada juz gildie!"));
                    return;
                }
                if(user.isOnline() && this.plugin.getProtectionManager().isProtected(user.getUuid())){
                    this.plugin.getProtectionManager().removeUser(user.getUuid());
                    TitleUtil.sendActionBar(user.asPlayer(), ChatUtil.fixColor("&4Twoja ochrona wlasnie wygasla!"));
                }
                this.plugin.getGuildManager().addMember(user, guild, GuildPermission.PLACE, GuildPermission.BREAK, GuildPermission.BASE_TELEPORT);
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopsByKind(TopKind.GUILD).stream().filter(sortable -> !sortable.getTopType().equals(TopType.GUILD_COINS)).forEach(sortable -> sortable.setSorted(false)));
                ChatUtil.sendMessage(sender, "&8>> {n}Dodales gracza {c}"+user.getName()+" {n}do gildii &8[&f"+guild.getTag()+"&8]");
            }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
