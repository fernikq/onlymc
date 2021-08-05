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

public class DiscoArmorCommand extends CustomCommand {

    private final CorePlugin plugin;

    public DiscoArmorCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
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
            if(!user.isDiscoArmorPermission()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnien do Disco Zbroi!"));
                return;
            }
            ChatUtil.sendMessage(player, "&8>> "+(user.isDiscoArmor() ? "&cWylaczyles" : "&aWlaczyles") + " "+" &fDisco Zbroje!");
            user.setDiscoArmor(!user.isDiscoArmor());
            if(user.isDiscoArmor()){
                this.plugin.getDiscoArmorManager().startTask(user);
                this.plugin.getDiscoArmorManager().getOriginalArmor().put(player.getUniqueId(), player.getInventory().getArmorContents());
                return;
            }
            this.plugin.getDiscoArmorManager().stopTask(player);
            if(this.plugin.getDiscoArmorManager().getOriginalArmor().containsKey(player.getUniqueId())){
                this.plugin.getDiscoArmorManager().restoreOriginalArmor(player);
            }
        });
        return true;
    }
}
