package pl.fernikq.core.command.admin.magicCase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.command.guild.player.*;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class AdminCaseCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Set<CustomCommand> customCommands;

    public AdminCaseCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.customCommands = new HashSet<>();
        this.customCommands.add(new CaseCreateCommand("create", new String[0], UserGroup.ADMIN, plugin));
        this.customCommands.add(new CaseRemoveCommand("remove", new String[0], UserGroup.ADMIN, plugin));
        this.customCommands.add(new CaseReloadCommand("reload", new String[0], UserGroup.ADMIN, plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            ChatUtil.sendMessage(sender, "&8&m--------&8[ {c}&lCASE &8]&m--------",
                    " ",
                    "&8>> {c}/case remove &8- &fUsuwa magiczna skrzynie",
                    "&8>> {c}/case create <premium, normal> &8- &fTworzy magiczna skrzynie o podanym typie",
                    "&8>> {c}/case reload &8- &fPonownie laduje dane magicznych skrzyn [drop itp.]",
                    " ",
                    "&8&m--------&8[ {c}&lCASE &8]&m--------");
            return true;
        }
        CustomCommand customCommand = getCommand(args[0]);
        if(customCommand == null){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany argument nie istnieje, wpisz /case aby uzyskac pomoc!"));
        }
        if(sender instanceof Player){
            Player player = (Player)sender;
            this.plugin.getUserManager().getUser(player.getUniqueId())
                    .onEmpty(() -> {
                        ChatUtil.sendMessage(sender, MessagesManager.errorMessage);
                        return;
                    }).peek(user -> {
                        if(!user.canByGroup(customCommand.getGroup())){
                            ChatUtil.sendMessage(sender, MessagesManager.commandErrorPermission);
                            return;
                        }
                        customCommand.onCommand(sender, command, label, args);
                    });
            return true;
        }
        customCommand.onCommand(sender, command, label, args);
        return true;
    }

    private CustomCommand getCommand(String argument){
        for(CustomCommand command : this.customCommands){
            if(command.getName().equalsIgnoreCase(argument) || command.getAliases().contains(argument)){
                return command;
            }
        }
        return null;
    }
}
