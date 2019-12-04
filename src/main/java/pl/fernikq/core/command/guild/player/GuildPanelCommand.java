package pl.fernikq.core.command.guild.player;

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

public class GuildPanelCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildPanelCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g panel"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(!user.hasGuild()){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
               return;
           }
           Guild guild = user.getGuild();
           if(!guild.getMemberByName(user.getName()).isPresent()){
               ChatUtil.sendMessage(sender, MessagesManager.commandErrorMessage);
               return;
           }
           this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
        });
        return true;
    }
}
