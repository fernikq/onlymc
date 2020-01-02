package pl.fernikq.core.command.guild.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildHealthCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildHealthCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/ga health <tag> <ilosc>"));
        }
        String tag = args[1].toUpperCase();
        int amount = Integer.parseInt(args[2]);
        if(amount < 1){
            amount = 1;
        }
        int finalAmount = amount;
        this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
            guild.setHealth(finalAmount);
            ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles ilosc zyc gildii {c}"+guild.getTag()+" {n}na {c}"+finalAmount);
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!")));
        return true;
    }
}
