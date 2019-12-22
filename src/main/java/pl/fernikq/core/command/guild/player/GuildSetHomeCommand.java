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

public class GuildSetHomeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildSetHomeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g sethome"));
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
            if(!member.hasPermission(GuildPermission.SET_BASE)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz uprawnienia do ustawiania bazy gildii!"));
                return;
            }
            this.plugin.getGuildManager().getGuildByLocation(player.getLocation().getBlock().getLocation()).peek(locationGuild -> {
                if(!locationGuild.equals(guild)){
                    ChatUtil.sendMessage(player, MessagesManager.error("Baze mozesz ustawic jedynie na terenie swojej gildii!"));
                    return;
                }
                guild.getRegion().setHome(player.getLocation());
                ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles {c}nowa {n}baze swojej {c}gidlii!");
            }).onEmpty(() -> ChatUtil.sendMessage(player, MessagesManager.error("Baze mozesz ustawic jedynie na terenie swojej gildii!")));
        });
        return true;
    }
}
