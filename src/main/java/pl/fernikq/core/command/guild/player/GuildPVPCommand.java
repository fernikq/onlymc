package pl.fernikq.core.command.guild.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class GuildPVPCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildPVPCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g pvp"));
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
                return;
            }
            Guild guild = user.getGuild();
            GuildMember member = guild.getMemberByName(user.getName()).orElse(null);
            if(member == null){
                ChatUtil.sendMessage(sender, MessagesManager.errorMessage);
                return;
            }
            if(!member.hasPermission(GuildPermission.PVP)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz uprawnienia do zarzadzania PVP!"));
                return;
            }
            ChatUtil.sendMessage(sender, "&8>> "+(guild.isFriendlyFire() ? "&cWylaczyles " : "&aWlaczyles ")+"{n}PVP w gildii!");
            guild.getOnlineMembers().stream().filter(onlineMember -> onlineMember.hasPermission(GuildPermission.PVP)).filter(onlineMember -> !onlineMember.equals(member)).forEach(onlineMember -> {
                ChatUtil.sendMessage(onlineMember.getUser().asPlayer(), "&8>> {n}Gracz {c}"+user.getName()+"{n}"+(guild.isFriendlyFire() ? " wylaczyl " : " wlaczyl ")+"{n}PVP w gildii!");
            });
            guild.setFriendlyFire(!guild.isFriendlyFire());
        });
        return true;
    }
}
