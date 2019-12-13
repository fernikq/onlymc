package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class IgnoreCommand extends CustomCommand {

    private final CorePlugin plugin;

    public IgnoreCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        if(args.length < 2){
            return ChatUtil.sendMessage(player, MessagesManager.usage("/ignore <tpa, msg> <nick>"));
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(args[0].equalsIgnoreCase("tpa")){
            this.plugin.getUserManager().getUser(args[1]).peek(otherUser -> {
                Set<User> blockedTpa = new HashSet<>(user.getUserChat().getBlockedTpa());
                if(blockedTpa.contains(otherUser)){
                    user.getUserChat().getBlockedTpa().remove(otherUser);
                    ChatUtil.sendMessage(sender, "&8>> {n}Prosby o teleportacja od gracza {c}"+otherUser.getName()+" {n}zostaly wlaczone!");
                    return;
                }
                if(otherUser.equals(user)){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz ignorowac samego siebie!"));
                    return;
                }
                if(otherUser.canByGroup(UserGroup.HELPER)){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz ignorowac tego gracza!"));
                    return;
                }
                user.getUserChat().getBlockedTpa().add(otherUser);
                ChatUtil.sendMessage(sender, "&8>> {n}Prosby o teleportacja od gracza {c}"+otherUser.getName()+" {n}zostaly wylaczone!");
                return;
            }).onEmpty(() -> ChatUtil.sendMessage(player, Lang.userNotExists));
            return true;
        }
        if(args[0].equalsIgnoreCase("msg")){
            this.plugin.getUserManager().getUser(args[1]).peek(otherUser -> {
                Set<User> blockedMessage = new HashSet<>(user.getUserChat().getBlockedMessage());
                if(blockedMessage.contains(otherUser)){
                    user.getUserChat().getBlockedMessage().remove(otherUser);
                    ChatUtil.sendMessage(sender, "&8>> {n}Prywatne wiadomosci od gracza {c}"+otherUser.getName()+" {n}zostaly wlaczone!");
                    return;
                }
                if(otherUser.equals(user)){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz ignorowac samego siebie!"));
                    return;
                }
                if(otherUser.canByGroup(UserGroup.HELPER)){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz ignorowac tego gracza!"));
                    return;
                }
                user.getUserChat().getBlockedMessage().add(otherUser);
                ChatUtil.sendMessage(sender, "&8>> {n}Prywatne wiadomosci od gracza {c}"+otherUser.getName()+" {n}zostaly wylaczone!");
                return;
            }).onEmpty(() -> ChatUtil.sendMessage(player, Lang.userNotExists));
            return true;
        }else{
            return ChatUtil.sendMessage(player, MessagesManager.usage("/ignore <tpa, msg> <nick>"));
        }
    }
}
