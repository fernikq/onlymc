package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.home.Home;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TeleportManager;

public class HomeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public HomeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.getHomes().isEmpty()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz domu!"));
                return;
            }
            if(args.length < 1){
                if(user.getHomes().size() == 1){
                    Home home = this.plugin.getHomeManager().get(user, 0);
                    this.plugin.getTeleportManager().teleportToLocation(player, home.getLocation(), ConfigManager.teleportHomeTime, "dom");
                    return;
                }
                ChatUtil.sendMessage(player, MessagesManager.error("Musisz podac nazwe domu, sprawdzisz je pod /homelist"));
                return;
            }
            String homeName = args[0];
            if(!this.plugin.getHomeManager().exists(user, homeName)){
                ChatUtil.sendMessage(player, MessagesManager.error("Dom o podanej nazwie nie istnieje, aby sprawdzic domy wpisz /homelist"));
                return;
            }
            Home home = this.plugin.getHomeManager().get(user, homeName);
            this.plugin.getTeleportManager().teleportToLocation(player, home.getLocation(), ConfigManager.teleportHomeTime, "dom o nazwie "+home.getName());
            return;
        });
        return true;
    }
}
