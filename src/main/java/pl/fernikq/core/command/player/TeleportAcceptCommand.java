package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class TeleportAcceptCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TeleportAcceptCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        if(args.length == 0){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
               if(user.getTpaRequests().asMap().isEmpty()){
                   ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz aktywnych prosb o teleportacje!"));
                   return;
               }
               User target = user.getTpaRequestsList().get(0);
               if(target.asPlayer() == null){
                   ChatUtil.sendMessage(player, Lang.playerOffline);
                   return;
               }
               user.getTpaRequests().asMap().remove(target);
               ChatUtil.sendMessage(player, "&8>> {n}Zaakceptowales prosbe o teleportacje gracza {c}"+target.getName());
               ChatUtil.sendMessage(target.asPlayer(), "&8>> {n}Twoja prosba o teleportacje zostala zaakceptowana przez {c}"+player.getName());
               this.plugin.getTeleportManager().teleportToPlayer(target.asPlayer(), player, ConfigManager.teleportTpaTime);
               return;
            });
            return true;
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.getTpaRequests().asMap().isEmpty()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz aktywnych prosb o teleportacje!"));
                return;
            }
            User target = this.plugin.getUserManager().getUser(args[0]).getOrNull();
            if(target == null){
                ChatUtil.sendMessage(player, Lang.userNotExists);
                return;
            }
            if(!user.getTpaRequests().asMap().containsKey(target)){
                ChatUtil.sendMessage(player,  MessagesManager.error("Nie posiadasz prosby o teleportacje od tego gracza!"));
                return;
            }
            if(target.asPlayer() == null){
                ChatUtil.sendMessage(player, Lang.playerOffline);
                return;
            }
            user.getTpaRequests().asMap().remove(target);
            ChatUtil.sendMessage(player, "&8>> {n}Zaakceptowales prosbe gracza {c}"+target.getName());
            ChatUtil.sendMessage(target.asPlayer(), "&8>> {n}Twoja prosba o teleportacje zostala zaakceptowana przez {c}"+player.getName());
            this.plugin.getTeleportManager().teleportToPlayer(target.asPlayer(), player, ConfigManager.teleportTpaTime);
            return;
        });
        return true;
    }
}
