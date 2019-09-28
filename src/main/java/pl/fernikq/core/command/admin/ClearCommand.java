package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class ClearCommand extends CustomCommand {

    private final CorePlugin plugin;

    public ClearCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
            }
            Player player = (Player)sender;
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            return ChatUtil.sendMessage(sender, "&8>> {n}Twoj ekwipunek zostal {c}wyczyszczony");
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        if(!target.equals(sender)){
            ChatUtil.sendMessage(sender, "&8>> {n}Wyczysciles ekwipunek gracza {c}"+target.getName());
        }
        return ChatUtil.sendMessage(target, "&8>> {n}Twoj ekwipunek zostal {c}wyczyszczony {n}przez {c}"+sender.getName());
    }
}
