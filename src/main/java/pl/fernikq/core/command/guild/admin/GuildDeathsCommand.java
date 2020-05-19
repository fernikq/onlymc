package pl.fernikq.core.command.guild.admin;

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

public class GuildDeathsCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildDeathsCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga deaths <nick> <ilosc>"));
        }
        String nick = args[1];
        if(!NumberUtil.isInt(args[2])){
            return ChatUtil.sendMessage(sender, Lang.badIntegerFormat);
        }
        int amount = Integer.parseInt(args[2]);
        if(amount < 0){
            amount = 0;
        }
        int finalAmount = amount;
        this.plugin.getUserManager().getUser(nick).peek(user -> {
            user.getUserStat().setDeaths(finalAmount);
            this.plugin.runAsync(() -> {
                this.plugin.getTopManager().getTopByType(TopType.USER_DEATHS).setSorted(false);
                if(user.hasGuild()){
                    this.plugin.getTopManager().getTopByType(TopType.GUILD_DEATHS).setSorted(false);
                }
                ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles ilosc smierci gracza {c}"+user.getName()+" {n}na {c}"+finalAmount);
            });
        }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
        return true;
    }
}
