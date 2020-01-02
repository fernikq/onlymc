package pl.fernikq.core.command.guild.admin;

import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.NumberUtil;

public class GuildItemsCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildItemsCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga itemy <nick>"));
        }
        String nick = args[1];
        Option.of(Bukkit.getPlayerExact(nick)).peek(player -> {
            this.plugin.getGuildManager().giveItems(player);
            ChatUtil.sendMessage(player, "&8>> {n}Otrzymales itemy na gildie od {c}"+sender.getName());
            if(!sender.equals(player)){
                ChatUtil.sendMessage(sender, "&8>> {n}Dales itemy na gildie graczowi  {c}"+player.getName());
            }
        }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.playerOffline));
        return true;
    }
}
