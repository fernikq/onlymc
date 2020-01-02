package pl.fernikq.core.command.guild;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.command.guild.admin.*;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class GuildAdminCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Set<CustomCommand> customCommands;

    public GuildAdminCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.customCommands = new HashSet<>();
        this.customCommands.add(new GuildAddMemberCommand("add", new String[]{"dodaj"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildAllianceBreakCommand("zerwij", new String[]{"rozwiaz"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildAllianceCreateCommand("sojusz", new String[0], UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildAssistsCommand("assists", new String[]{"asysty"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildDeathsCommand("deaths", new String[]{"smierci"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildDeleteCommand("delete", new String[]{"usun"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildHealthCommand("health", new String[]{"zycia", "zycie"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildItemsCommand("items", new String[]{"itemy"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildKillsCommand("kills", new String[]{"zabojstwa"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildLeaderCommand("lider", new String[]{"leader"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildLogoutsCommand("logouts", new String[]{"logouty"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildPointsCommand("points", new String[]{"punkty"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildRemoveMemberCommand("remove", new String[]{"wyrzuc"}, UserGroup.ADMIN, this.plugin));
        this.customCommands.add(new GuildTeleportCommand("tp", new String[]{"home", "baza"}, UserGroup.ADMIN, this.plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            for(String help : MessagesManager.guildAdminMainCommandHelp){
                ChatUtil.sendMessage(sender, help);
            }
            return true;
        }
        CustomCommand customCommand = getCommand(args[0]);
        if(customCommand == null){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany argument nie istnieje, wpisz /ga aby uzyskac pomoc!"));
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
