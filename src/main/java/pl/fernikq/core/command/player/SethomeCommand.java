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

public class SethomeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SethomeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length == 0){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                if(user.getHomes().isEmpty()){
                    this.plugin.getHomeManager().create(user, "home", player.getLocation());
                    ChatUtil.sendMessage(player, "&8>> {n}Ustawiles dom o nazwie {c}home");
                    return;
                }else{
                    ChatUtil.sendMessage(player, MessagesManager.usage("/sethome <nazwa>"));
                    return;
                }
            });
            return true;
        }
        String homeName = args[0].toLowerCase();
        if(homeName.length() > 32){
            return ChatUtil.sendMessage(player, MessagesManager.error("Nazwa nie moze byc dluzsza niz 32 znaki!"));
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!this.plugin.getHomeManager().canCreate(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz maksymalna ilosc domow!"));
                return;
            }
            if(this.plugin.getHomeManager().exists(user, homeName)){
                Home home = this.plugin.getHomeManager().get(user, homeName);
                home.setLocation(player.getLocation());
                this.plugin.getHomeManager().getHomeData().update(home);
                ChatUtil.sendMessage(player, "&8>> {n}Zmieniles lokalizacje domu o nazwie {c}"+homeName);
                return;
            }
            this.plugin.getHomeManager().create(user, homeName, player.getLocation());
            ChatUtil.sendMessage(player, "&8>> {n}Ustawiles dom o nazwie {c}"+homeName);
            return;
            });
        return true;
    }
}
