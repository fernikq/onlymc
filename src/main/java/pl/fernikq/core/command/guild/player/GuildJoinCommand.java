package pl.fernikq.core.command.guild.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

public class GuildJoinCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildJoinCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g dolacz <tag>"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(user.hasGuild()){
               ChatUtil.sendMessage(sender, MessagesManager.error("Posiadasz juz gildie!"));
               return;
           }
           Guild guild = this.plugin.getGuildManager().getGuildByTag(args[1]).getOrNull();
           if(guild == null){
               ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!"));
               return;
           }
           if(!guild.getMembersRequest().asMap().containsKey(user)){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz zaproszenia od podanej gildii!"));
               return;
           }
           if(guild.getMembers().size() >= guild.getMaxMembers()){
               ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia osiagnela maksymalna ilosc czlonkow!"));
               return;
           }
           guild.getMembersRequest().asMap().remove(user);
           String message = MessagesManager.guildJoinMessage;
           message = message.replace("{TAG}", guild.getTag());
           message = message.replace("{PLAYER}", player.getName());
           String finalMessage = message;
           this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isGuildMessages()).forEach(onlineUser -> ChatUtil.sendMessage(onlineUser.asPlayer(), finalMessage));
           this.plugin.getGuildManager().addMember(user, guild, GuildPermission.PLACE, GuildPermission.BREAK, GuildPermission.BASE_TELEPORT);
           this.plugin.runAsync(() -> this.plugin.getTopManager().getTopsByKind(TopKind.GUILD).stream().filter(sortable -> !sortable.getTopType().equals(TopType.GUILD_COINS)).forEach(sortable -> sortable.setSorted(false)));
        });
        return true;
    }
}
