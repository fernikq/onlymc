package pl.fernikq.core.command.guild;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.command.guild.player.*;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class GuildCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Set<CustomCommand> customCommands;

    public GuildCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.customCommands = new HashSet<>();
        this.customCommands.add(new GuildCreateCommand("zaloz", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildDeleteCommand("usun", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildInviteCommand("zapros", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildJoinCommand("dolacz", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildQuitCommand("opusc", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildKickCommand("wyrzuc", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildItemsCommand("itemy", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildLeaderCommand("lider", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildPVPCommand("pvp", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildAllianceCreateCommand("sojusz", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildAllianceBreakCommand("zerwij", new String[]{"rozwiaz"}, UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildHomeCommand("home", new String[]{"baza", "dom"}, UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildSetHomeCommand("sethome", new String[]{"setbaza", "setdom"}, UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildPanelCommand("panel", new String[0], UserGroup.PLAYER, this.plugin));
        this.customCommands.add(new GuildTreasureCommand("skarbiec", new String[0], UserGroup.PLAYER, this.plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            for(String help : MessagesManager.guildMainCommandHelp){
                ChatUtil.sendMessage(sender, help);
            }
            return true;
        }
        CustomCommand customCommand = getCommand(args[0]);
        if(customCommand == null){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany argument nie istnieje, wpisz /gildia aby uzyskac pomoc!"));
        }
        if(sender instanceof Player){
            Player player = (Player)sender;
            this.plugin.getUserManager().getUser(player.getUniqueId())
                    .onEmpty(() -> {
                        ChatUtil.sendMessage(sender, MessagesManager.commandErrorMessage);
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
