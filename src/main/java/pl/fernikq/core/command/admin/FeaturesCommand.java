package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class FeaturesCommand extends CustomCommand {

    private final CorePlugin plugin;

    public FeaturesCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/dodatki <disco, rainbow> <gracz> <on, off>"));
        }
        switch(args[0].toLowerCase()){
            case "disco":{
                this.plugin.getUserManager().getUser(args[1]).peek(user -> {
                    if(args[2].equalsIgnoreCase("off")){
                        user.setDiscoArmorPermission(false);
                        user.setDiscoArmor(false);
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getUserManager().getUserData().updateDiscoArmor(user));
                        ChatUtil.sendMessage(sender, "&8>> &fGracz {c}"+user.getName()+" &fstracil dostep do DiscoZbroi.");
                        return;
                    }
                    else if(args[2].equalsIgnoreCase("on")){
                        user.setDiscoArmorPermission(true);
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getUserManager().getUserData().updateDiscoArmor(user));
                        ChatUtil.sendMessage(sender, "&8>> &fGracz {c}"+user.getName()+" &fotrzymal dostep do DiscoZbroi.");
                        return;
                    }else{
                        ChatUtil.sendMessage(sender, MessagesManager.usage("/dodatki <disco, rainbow> <gracz> <on, off>"));
                        return;
                    }
                }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
                return true;
            }
            case "rainbow":{
                this.plugin.getUserManager().getUser(args[1]).peek(user -> {
                    if(args[2].equalsIgnoreCase("off")){
                        user.setRainbowNicknamePermission(false);
                        user.setRainbowNickname(false);
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getUserManager().getUserData().updateRainbowNick(user));
                        ChatUtil.sendMessage(sender, "&8>> &fGracz {c}"+user.getName()+" &fstracil dostep do Teczowego Nicku.");
                        return;
                    }
                    else if(args[2].equalsIgnoreCase("on")){
                        user.setRainbowNicknamePermission(true);
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getUserManager().getUserData().updateRainbowNick(user));
                        ChatUtil.sendMessage(sender, "&8>> &fGracz {c}"+user.getName()+" &fotrzymal dostep do Teczowego Nicku.");
                        return;
                    }else{
                        ChatUtil.sendMessage(sender, MessagesManager.usage("/dodatki <disco, rainbow> <gracz> <on, off>"));
                        return;
                    }
                }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
                return true;
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/dodatki <disco, nick> <gracz> <on, off>"));
            }
        }
    }
}
