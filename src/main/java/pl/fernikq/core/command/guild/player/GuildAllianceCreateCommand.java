package pl.fernikq.core.command.guild.player;

import org.bukkit.Bukkit;
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

public class GuildAllianceCreateCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildAllianceCreateCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g sojusz <tag>"));
        }
        String tag = args[1].toUpperCase();
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz gildii!"));
                return;
            }
            Guild guild = user.getGuild();
            GuildMember member = guild.getMemberByName(user.getName()).orElse(null);
            if(member == null){
                ChatUtil.sendMessage(sender, MessagesManager.commandErrorMessage);
                return;
            }
            if(!member.hasPermission(GuildPermission.ALLIES)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz uprawnienia do zarzadzania sojuszami!"));
                return;
            }
            Guild targetGuild = this.plugin.getGuildManager().getGuildByTag(tag).getOrNull();
            if(targetGuild == null){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podana gildia nie istnieje!"));
                return;
            }
            if(targetGuild.equals(guild)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz zawiazac sojuszu z wlasna gildia!"));
                return;
            }
            if(this.plugin.getAllianceManager().hasAlliance(guild, targetGuild)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Twoja gildia posiada juz sojusz z podana gildia!"));
                return;
            }
            if(targetGuild.getAlliesRequest().asMap().containsKey(guild)){
                targetGuild.getAlliesRequest().asMap().remove(guild);
                this.plugin.getAllianceManager().createAlliance(guild, targetGuild);
                String message = MessagesManager.guildAllianceCreateMessage;
                message = message.replace("{TAG1}", guild.getTag());
                message = message.replace("{TAG2}", targetGuild.getTag());
                String finalMessage = message;
                Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
                return;
            }
            if(guild.getAlliesRequest().asMap().containsKey(targetGuild)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Twoja gildia wyslala juz zaproszenie do sojuszu podanej gildii!"));
                return;
            }
            guild.getAlliesRequest().put(targetGuild, 100L);
            ChatUtil.sendMessage(sender, "&8>> {n}Wyslales zaproszenie do sojuszu gildii &8[{c}"+targetGuild.getTag()+"&8]");
            targetGuild.getOnlineMembers().stream().filter(onlineMember -> onlineMember.hasPermission(GuildPermission.ALLIES)).forEach(onlineMember -> {
                ChatUtil.sendMessage(onlineMember.getUser().asPlayer(), "&8>> {n}Twoja gildia otrzymala zaproszenie do sojuszu od gildii &8[{c}"+guild.getTag()+"&8], {n}aby zaakceptowac "+
                        "wpisz {c}/g sojusz "+guild.getTag());
            });
        });
        return true;
    }
}
