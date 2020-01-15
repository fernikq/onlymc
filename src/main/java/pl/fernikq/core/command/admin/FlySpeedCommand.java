package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.NumberUtil;

public class FlySpeedCommand extends CustomCommand {

    private final CorePlugin plugin;

    public FlySpeedCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/fspeed <predkosc> <gracz>"));
        }
        if(!NumberUtil.isFloat(args[0])){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany argument nie jest liczba!"));
        }
        float speed = Float.parseFloat(args[0]);
        if(speed > 10){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Zakres wartosci to 1-10"));
        }
        if(speed < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Zakres wartosci to 1-10"));
        }
        if(args.length == 1){
            Player player = (Player)sender;
            player.setFlySpeed(speed/10);
            return ChatUtil.sendMessage(player, "&8>> {n}Ustawiles swoja predkosc latania na {c}"+speed);
        }
        else if(args.length == 2){
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null){
                return ChatUtil.sendMessage(sender, Lang.playerOffline);
            }
            target.setFlySpeed(speed/10);
            if(!target.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles predkosc latania gracza {c}"+target.getName()+" {n}na {c}"+speed);
            }
            return ChatUtil.sendMessage(target, "&8>> {n}Twoja predkosc latania zostala ustawiona na {c}"+speed+" {n}przez {c}"+sender.getName());
        }else{
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/fspeed <predkosc> <gracz>"));
        }
    }
}
