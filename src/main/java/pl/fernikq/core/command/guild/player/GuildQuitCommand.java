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
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildQuitCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildQuitCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g opusc"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(!user.hasGuild()){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
               return;
           }
           Guild guild = user.getGuild();
           if(guild.getOwner().equals(user)){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz opuscic gildii jako lider!"));
               return;
           }
           GuildMember member = guild.getMemberByName(user.getName()).orElse(null);
           if(member == null){
               ChatUtil.sendMessage(sender, MessagesManager.commandErrorMessage);
               return;
           }
           String message = MessagesManager.guildQuitMessage;
           message = message.replace("{TAG}", guild.getTag());
           message = message.replace("{PLAYER}", player.getName());
           String finalMessage = message;
           Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
           this.plugin.getGuildManager().removeMember(member);
        });
        return true;
    }
}
