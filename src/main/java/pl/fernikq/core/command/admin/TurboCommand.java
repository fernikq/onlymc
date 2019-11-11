package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

public class TurboCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TurboCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/turbo <drop, exp> <gracz> <czas>"));
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if(player == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        long time = TimeUtil.getTime(args[2]);
        if(time == 0L){
            return ChatUtil.sendMessage(sender, Lang.badTimeFormat);
        }
        switch(args[0].toLowerCase()){
            case "drop":{
                this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> user.getUserStat().setTurboDropTime(time + System.currentTimeMillis()));
                Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8>> {c}&lTurboDrop {n}dla gracza {c}"+player.getName()+" {n}zostal aktywowany na {c}"+TimeUtil.getTimeToString((time + System.currentTimeMillis()) - System.currentTimeMillis())+" {n}przez {c}"+sender.getName()));
                return true;
            }
            case "xp":
            case "exp":{
                this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> user.getUserStat().setTurboExpTime(time + System.currentTimeMillis()));
                Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8>> {c}&lTubroExp {n}dla gracza {c}"+player.getName()+" {n}zostal aktywowany na {c}"+TimeUtil.getTimeToString((time + System.currentTimeMillis()) - System.currentTimeMillis())+" {n}przez {c}"+sender.getName()));
                return true;
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/turbo <drop, exp> <gracz> <czas>"));
            }
        }
    }
}
