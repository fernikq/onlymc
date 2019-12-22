package pl.fernikq.core.command.guild.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildInviteCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildInviteCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g zapros <nick>"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
                return;
            }
            Guild guild = user.getGuild();
            GuildMember member = guild.getMemberByName(user.getName()).orElse(null);
            if(member == null){
                ChatUtil.sendMessage(sender, MessagesManager.errorMessage);
                return;
            }
            if(!member.hasPermission(GuildPermission.INVITE)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz uprawnienia do zapraszania graczy!"));
                return;
            }
            User targetUser = this.plugin.getUserManager().getUser(args[1]).getOrNull();
            if(targetUser == null){
                ChatUtil.sendMessage(sender, Lang.userNotExists);
                return;
            }
            if(targetUser.equals(user)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz zaprosic samego siebie!"));
                return;
            }
            if(targetUser.hasGuild()){
                if(targetUser.getGuild().equals(guild)){
                    ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nalezy juz do twojej gildii!"));
                    return;
                }
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz posiada juz inna gildie!"));
                return;
            }
            if(guild.getMembers().size() >= guild.getMaxMembers()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Twoja gildia osiagnela maksymalna ilosc czlonkow!"));
                return;
            }
            if(guild.getMembersRequest().asMap().containsKey(targetUser)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz posiada juz zaproszenie od twojej gildii!"));
                return;
            }
            guild.getMembersRequest().put(targetUser, 100L);
            ChatUtil.sendMessage(sender, "&8>> {n}Zaprosiles gracza {c}"+targetUser.getName()+" {n}do swojej gildii!");
            ChatUtil.sendMessage(targetUser.asPlayer(), MessagesManager.guildInviteMessage.replace("{TAG}", guild.getTag()));
        });
        return true;
    }
}
