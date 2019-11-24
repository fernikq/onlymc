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
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g usun"));
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(!user.hasGuild()){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
               return;
           }
           Guild guild = user.getGuild();
           if(!guild.getOwner().equals(user)){
               ChatUtil.sendMessage(sender, MessagesManager.error("Aby to zrobic musisz byc zalozycielem gildii!"));
               return;
           }
           if(!guild.getPreDeleted().asMap().containsKey(guild)){
               guild.getPreDeleted().put(guild, 100L);
               ChatUtil.sendMessage(sender, "&8>> {n}Wpisz {c}/g usun {n}aby potwierdzic!");
               return;
           }
           this.plugin.getGuildManager().deleteGuild(guild);
           String message = MessagesManager.guildDeleteMessage;
           message = message.replace("{TAG}", guild.getTag());
           message = message.replace("{NAME}", guild.getName());
           message = message.replace("{OWNER}", player.getName());
           String finalMessage = message;
           Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
        });
        return true;
    }
}
