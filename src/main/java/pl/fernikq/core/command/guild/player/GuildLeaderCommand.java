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
import pl.fernikq.core.user.User;
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
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g lider <nick>"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
                return;
            }
            Guild guild = user.getGuild();
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Musisz byc zalozycielem aby to zrobic!"));
                return;
            }
            User targetUser = this.plugin.getUserManager().getUser(args[1]).getOrNull();
            if(targetUser == null){
                ChatUtil.sendMessage(sender, Lang.userNotExists);
                return;
            }
            if(targetUser.equals(user)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz nadac lidera samemu sobie!"));
                return;
            }
            if(!targetUser.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nie posiada gildii!"));
                return;
            }
            if(!targetUser.getGuild().equals(guild)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nie nalezy do twojej gildii!"));
                return;
            }
            guild.setOwner(targetUser);
            String message = MessagesManager.guildLeaderMessage;
            message = message.replace("{TAG}", guild.getTag());
            message = message.replace("{PLAYER}", targetUser.getName());
            String finalMessage = message;
            Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
        });
        return true;
    }
}
