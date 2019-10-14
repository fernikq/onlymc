package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class TeleportCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TeleportCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1){
            if(!(sender instanceof Player)){
                return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
            }
            Player player = (Player)sender;
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null){
                return ChatUtil.sendMessage(sender, Lang.playerOffline);
            }
            player.teleport(target);
            return ChatUtil.sendMessage(sender, "&8>> {n}Zostales przeteleportowany do gracza {c}"+target.getName());
        }
        else if(args.length == 2){
            Player player = Bukkit.getPlayerExact(args[0]);
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null || player == null){
                return ChatUtil.sendMessage(sender, Lang.playerOffline);
            }
            player.teleport(target);
            if(!player.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> {n}Przeteleportowales gracza {c}"+player.getName()+" {n}do gracza {c}"+target.getName());
            }
            return ChatUtil.sendMessage(player, "&8>> {n}Zostales przeteleportowany do gracza {c}"+target.getName()+" {n}przez {c}"+sender.getName());
        }
        else if(args.length == 3){
            if(!(sender instanceof Player)){
                return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
            }
            Player player = (Player)sender;
            if(!NumberUtil.isInt(args[0]) || !NumberUtil.isInt(args[1]) || !NumberUtil.isInt(args[2])){
                return ChatUtil.sendMessage(sender, MessagesManager.error("Podales niepoprawne koordynaty!"));
            }
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            Location l = new Location(player.getWorld(), x, y, z);
            player.teleport(l);
            return ChatUtil.sendMessage(player, "&8>> {n}Zostales przeteleportowany na koordynaty&8: {c}X&8: {n}"+x+" {c}Y&8: {n}"+y+" {c}Z&8: {n}"+z);
        }
        else if(args.length == 4){
            Player target = Bukkit.getPlayerExact(args[0]);
            if(!NumberUtil.isInt(args[1]) || !NumberUtil.isInt(args[2]) || !NumberUtil.isInt(args[3])){
                return ChatUtil.sendMessage(sender, MessagesManager.error("Podales niepoprawne koordynaty!"));
            }
            if(target == null){
                return ChatUtil.sendMessage(sender, Lang.playerOffline);
            }
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            Location l = new Location(target.getWorld(), x, y, z);
            if(!target.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> {n}Przeteleportowales gracza {c}"+target.getName()+" {n}na koordynaty&8: {c}X&8: {n}"+x+" {c}Y&8: {n}"+y+" {c}Z&8: {n}"+z);
            }
            target.teleport(l);
            return ChatUtil.sendMessage(target, "&8>> {n}Zostales przeteleportowany na koordynaty&8: {c}X&8: {n}"+x+" {c}Y&8: {n}"+y+" {c}Z&8: {n}"+z+" {n}przez {c}"+sender.getName());
        }else{
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/tp <gracz>"));
        }
    }
}
