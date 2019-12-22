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
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildKickCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildKickCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g wyrzuc <nick>"));
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
            if(!member.hasPermission(GuildPermission.KICK)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz uprawnienia do wyrzucania graczy!"));
                return;
            }
            User targetUser = this.plugin.getUserManager().getUser(args[1]).getOrNull();
            if(targetUser == null){
                ChatUtil.sendMessage(sender, Lang.userNotExists);
                return;
            }
            if(!targetUser.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nie posiada gildii!"));
                return;
            }
            Guild targetGuild = targetUser.getGuild();
            if(!targetGuild.equals(guild)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nalezy do innej gildii!"));
                return;
            }
            if(targetUser.equals(user)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz wyrzucic samego siebie!"));
                return;
            }
            if(guild.getOwner().equals(targetUser)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz wyrzucic zalozyciela!"));
                return;
            }
            GuildMember targetMember = guild.getMemberByName(targetUser.getName()).orElse(null);
            if(targetMember == null){
                ChatUtil.sendMessage(sender, MessagesManager.errorMessage);
                return;
            }
            String message = MessagesManager.guildKickMessage;
            message = message.replace("{TAG}", guild.getTag());
            message = message.replace("{PLAYER}", targetUser.getName());
            String finalMessage = message;
            Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
            this.plugin.getGuildManager().removeMember(targetMember);
        });
        return true;
    }
}
