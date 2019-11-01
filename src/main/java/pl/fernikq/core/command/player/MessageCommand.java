package pl.fernikq.core.command.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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

public class MessageCommand extends CustomCommand {

    private final CorePlugin plugin;

    public MessageCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.error("/msg <nick> <wiadomosc>"));
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            String message = StringUtils.join(args, " ", 1, args.length);
            if(user.canByGroup(UserGroup.HELPER)){
                message = ChatUtil.fixColor(message);
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null){
                ChatUtil.sendMessage(sender, Lang.playerOffline);
                return;
            }
            User targetUser = this.plugin.getUserManager().getUser(target.getUniqueId()).getOrNull();
            if(targetUser == null){
                ChatUtil.sendMessage(player, Lang.userNotExists);
                return;
            }
            user.setPrivateMessageSender(targetUser);
            targetUser.setPrivateMessageSender(user);
            player.sendMessage(ChatUtil.fixColor(MessagesManager.playerPrivateMessageFormat.replace("{SENDER}", "Ja")
                    .replace("{RECEIVER}", target.getName()))+message);
            target.sendMessage(ChatUtil.fixColor(MessagesManager.playerPrivateMessageFormat.replace("{SENDER}", player.getName())
                    .replace("{RECEIVER}", "Ja"))+message);
        });
        return true;
    }
}
