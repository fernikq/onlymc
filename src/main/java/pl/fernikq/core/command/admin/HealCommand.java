package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class HealCommand extends CustomCommand {

    private final CorePlugin plugin;

    public HealCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
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
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);
            return ChatUtil.sendMessage(sender, "&8>> {n}Zostales {c}uleczony");
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setFireTicks(0);
        if(!target.equals(sender)){
            ChatUtil.sendMessage(sender, "&8>> {c}Uleczyles {n}gracza {c}"+target.getName());
        }
        return ChatUtil.sendMessage(target, "&8>> {n}Zostales {c}uleczony {n}przez {c}"+sender.getName());
    }
}
