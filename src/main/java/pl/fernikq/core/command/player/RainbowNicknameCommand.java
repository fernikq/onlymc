package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class RainbowNicknameCommand extends CustomCommand {

    private final CorePlugin plugin;

    public RainbowNicknameCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.isRainbowNicknamePermission()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnien do teczowego nicku!"));
                return;
            }
            ChatUtil.sendMessage(player, "&8>> "+(user.isRainbowNickname() ? "&cWylaczyles" : "&aWlaczyles") + " "+" &fteczowy nick!");
            user.setRainbowNickname(!user.isRainbowNickname());
        });
        return true;
    }
}
