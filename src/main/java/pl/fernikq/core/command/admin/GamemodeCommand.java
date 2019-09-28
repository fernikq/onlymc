package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GamemodeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GamemodeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/gamemode <tryb>"));
        }
        GameMode gameMode = getGameMode(args[0]);
        if(gameMode == null){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany tryb gry nie istnieje!"));
        }
        if(args.length == 1){
            if(!(sender instanceof Player)){
                return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
            }
            Player player = (Player)sender;
            player.setGameMode(gameMode);
            return ChatUtil.sendMessage(player, "&8>> {n}Zmieniles swoj tryb gry na {c}"+gameMode.name());
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        target.setGameMode(gameMode);
        if(!target.equals(sender)){
            ChatUtil.sendMessage(sender, "&8>> {n}Zmieniles tryb gry gracza {c}"+target.getName()+" {n} na {c}"+gameMode.name());
        }
        return ChatUtil.sendMessage(target, "&8>> {n}Twoj tryb gry zostal zmieniony na {c}"+gameMode.name()+" {n}przez {c}"+sender.getName());
    }

    private GameMode getGameMode(String mode){
        if(mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c")){
            return GameMode.CREATIVE;
        }
        if(mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s")){
            return GameMode.SURVIVAL;
        }
        if(mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a")){
            return GameMode.ADVENTURE;
        }
        if(mode.equalsIgnoreCase("3")){
            return GameMode.SPECTATOR;
        }
        return null;
    }
}
