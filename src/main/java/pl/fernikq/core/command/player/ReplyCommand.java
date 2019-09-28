package pl.fernikq.core.command.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class ReplyCommand extends CustomCommand {

    private final CorePlugin plugin;

    public ReplyCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.error("/reply <wiadomosc>"));
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           String message = StringUtils.join(args, " ", 0, args.length);
           if(user.canByGroup(UserGroup.HELPER)){
               message = ChatUtil.fixColor(message);
           }
           if(user.getPrivateMessageSender() == null){
               ChatUtil.sendMessage(sender, MessagesManager.error("Nie masz komu odpisac!"));
               return;
           }
           if(user.getPrivateMessageSender().asPlayer() == null){
               ChatUtil.sendMessage(sender, MessagesManager.error("Gracz jest offline!"));
               return;
           }
           user.getPrivateMessageSender().setPrivateMessageSender(user);
           player.sendMessage(ChatUtil.fixColor(MessagesManager.playerPrivateMessageFormat.replace("{SENDER}", "Ja")
                   .replace("{RECEIVER}", user.getPrivateMessageSender().getName()))+message);
           user.getPrivateMessageSender().asPlayer().sendMessage(ChatUtil.fixColor(MessagesManager.playerPrivateMessageFormat.replace("{SENDER}", player.getName())
                   .replace("{RECEIVER}", "Ja"))+message);
        });
        return true;
    }
}
