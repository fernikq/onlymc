package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.home.Home;
import pl.fernikq.core.util.ChatUtil;

public class DelhomeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public DelhomeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/delhome <nazwa>"));
        }
        Player player = (Player)sender;
        String homeName = args[0];
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(!this.plugin.getHomeManager().exists(user, homeName)){
               ChatUtil.sendMessage(user.asPlayer(), MessagesManager.error("Dom o podanej nazwie nie istnieje, aby sprawdzic domy wpisz /homelist"));
               return;
           }
           Home home = this.plugin.getHomeManager().get(user, homeName);
           this.plugin.getHomeManager().delete(user, home);
           ChatUtil.sendMessage(user.asPlayer(), "&8>> {n}Pomyslnie usunales dom o nazwie {c}"+home.getName());
           return;
        });
        return true;
    }
}
