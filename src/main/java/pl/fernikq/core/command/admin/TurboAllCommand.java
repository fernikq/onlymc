package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.*;

public class TurboAllCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TurboAllCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/turboall <drop, exp> <czas>"));
        }
        long time = TimeUtil.getTime(args[1]);
        if(time == 0L){
            return ChatUtil.sendMessage(sender, Lang.badTimeFormat);
        }
        switch(args[0].toLowerCase()){
            case "drop":{
                ConfigManager.turboDropTime = time + System.currentTimeMillis();
                this.plugin.getConfigManager().save();
                Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8>> {c}&lTurboDrop {n}dla serwera zostal aktywowany na {c}"+TimeUtil.getTimeToString((time + System.currentTimeMillis()) - System.currentTimeMillis())+" {n}przez {c}"+sender.getName()));
                return true;
            }
            case "xp":
            case "exp":{
                ConfigManager.turboExpTime = time + System.currentTimeMillis();
                this.plugin.getConfigManager().save();
                Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8>> {c}&lTubroExp {n}dla serwera zostal aktywowany na {c}"+TimeUtil.getTimeToString((time + System.currentTimeMillis()) - System.currentTimeMillis())+" {n}przez {c}"+sender.getName()));
                return true;
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/turboall <drop, exp> <czas>"));
            }
        }
    }
}
