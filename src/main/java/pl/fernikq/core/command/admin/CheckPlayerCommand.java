package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.check.PlayerCheckUtil;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.TitleUtil;

public class CheckPlayerCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CheckPlayerCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/sprawdz <setplace, gracz, czysty> <gracz>"));
        }
        switch(args[0].toLowerCase()){
            case "setplace":{
                if(!(sender instanceof Player)){
                    return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
                }
                ConfigManager.playerCheckPlaceLocation = LocationUtil.locationToString(((Player)sender).getLocation());
                this.plugin.getConfigManager().save();
                return ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles miejsce do sprawdzania!");
            }
            case "czysty":{
                if(args.length < 2){
                    return ChatUtil.sendMessage(sender, MessagesManager.usage("/sprawdz czysty <gracz>"));
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if(player == null){
                    return ChatUtil.sendMessage(sender, Lang.playerOffline);
                }
                if(!PlayerCheckUtil.getPlayerSet().contains(player)){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz nie jest sprawdzany!"));
                }
                PlayerCheckUtil.getPlayerSet().remove(player);
                player.teleport(LocationUtil.locationFromString(ConfigManager.spawnLocation));
                return ChatUtil.sendMessage(sender, "&8>> {n}Gracz {c}"+player.getName()+" {n}zostal uznany za czystego!");
            }
            default:{
                Player player = Bukkit.getPlayerExact(args[0]);
                if(player == null){
                    return ChatUtil.sendMessage(sender, Lang.playerOffline);
                }
                if(PlayerCheckUtil.getPlayerSet().contains(player)){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz jest juz sprawdzany!"));
                }
                PlayerCheckUtil.getPlayerSet().add(player);
                player.teleport(LocationUtil.locationFromString(ConfigManager.playerCheckPlaceLocation));
                TitleUtil.sendTitle(player, ChatUtil.fixColor("&c&lSprawdzanie"), 10);
                TitleUtil.sendSubTitle(player, ChatUtil.fixColor("&fJestes sprawdzany!"), 10);
                return ChatUtil.sendMessage(sender, "&8>> {n}Gracz {c}"+player.getName()+" {n}jest sprawdzany!");
            }
        }
    }
}
