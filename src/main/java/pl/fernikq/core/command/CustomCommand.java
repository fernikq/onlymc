package pl.fernikq.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.Arrays;
import java.util.List;

public abstract class CustomCommand implements CommandExecutor {

    private String name;
    private String usage;
    private List<String> aliases;
    private UserGroup group;
    private CorePlugin plugin;

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public CustomCommand(String name, String usage, String[] aliases, UserGroup group, CorePlugin plugin){
        this.name = name;
        this.usage = usage;
        this.group = group;
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
    }

    public CustomCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        this.name = name;
        this.group = group;
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
    }

    public CustomCommand(String name, String[] aliases, CorePlugin plugin){
        this.name = name;
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void register(){
        this.plugin.getCommandManager().register(this);
    }

    public ReflectCommand getCommand(){
        ReflectCommand reflectCommand = new ReflectCommand(this.name, this.plugin);
        if(this.aliases != null){
            reflectCommand.setAliases(this.aliases);
        }
        if(this.usage != null){
            reflectCommand.setUsage(this.usage);
        }
        reflectCommand.setExecutor(this);
        return reflectCommand;
    }

    private final class ReflectCommand extends Command {

        private CorePlugin plugin;
        private CustomCommand executor;
        private MessagesManager messages;

        public ReflectCommand(String command, CorePlugin plugin){
            super(command);
            this.plugin = plugin;
            this.messages = plugin.getMessagesManager();
            this.executor = null;
        }

        public void setExecutor(CustomCommand customCommand){
            this.executor = customCommand;
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if(sender instanceof Player){
                Player player = (Player)sender;
                this.plugin.getUserManager().getUser(player.getUniqueId())
                        .onEmpty(() -> {
                            ChatUtil.sendMessage(sender, messages.getCommandErrorMessage());
                            return;
                        })
                        .peek(user -> {
                            if(!user.canByGroup(this.executor.getGroup())){
                                ChatUtil.sendMessage(sender, messages.getCommandErrorPermission());
                                return;
                            }
                            if(this.executor != null){
                                this.executor.onCommand(sender, this, label, args);
                            }
                        });
                return true;
            }
            if(this.executor != null){
                this.executor.onCommand(sender, this, label, args);
            }
            return true;
        }
    }
}
