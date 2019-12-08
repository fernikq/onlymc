package pl.fernikq.core.command.simpleCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.List;

public class SimpleCustomCommand extends CustomCommand {

    private final CorePlugin plugin;
    private final SimpleCommand simpleCommand;

    public SimpleCustomCommand(String name, List<String> aliases, UserGroup group, CorePlugin plugin, SimpleCommand simpleCommand){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.simpleCommand = simpleCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for(String message : simpleCommand.getMessages()){
            ChatUtil.sendMessage(sender, message);
        }
        return true;
    }

    public SimpleCommand getSimpleCommand() {
        return simpleCommand;
    }
}
