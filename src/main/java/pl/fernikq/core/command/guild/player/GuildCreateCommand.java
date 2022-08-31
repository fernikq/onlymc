package pl.fernikq.core.command.guild.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.StringUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.fernikq.core.util.TitleUtil;

import java.util.HashSet;
import java.util.Set;

public class GuildCreateCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildCreateCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(ConfigManager.guildCreateBlockTime > System.currentTimeMillis()){
            return ChatUtil.sendMessage(player, MessagesManager.error("Zakladanie gildii zablokowane jest jeszcze przez "+ TimeUtil.getTimeToString(ConfigManager.guildCreateBlockTime - System.currentTimeMillis())));
        }
        if(args.length < 3){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g zaloz <tag> <nazwa>"));
        }
        String tag = args[1].toUpperCase();
        String name = args[2];
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.hasGuild()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Posiadasz juz gildie!"));
                return;
            }
            if(!StringUtil.isAlphaNumeric(tag) || !StringUtil.isAlphaNumeric(name)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nazwa lub tag przez ciebie podany posiada niedozwolone znaki!"));
                return;
            }
            if(tag.length() > 5 || tag.length() < 2){
                ChatUtil.sendMessage(sender, MessagesManager.error("Tag musi posiadac od 2 do 5 znakow!"));
                return;
            }
            if(name.length() < 6 || name.length() > 26){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nazwa musi posiadac od 6 do 26 znakow!"));
                return;
            }
            if(this.plugin.getGuildManager().getGuildByTag(tag).isDefined()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Gildia o podanym tagu juz istnieje!"));
                return;
            }
            if(this.plugin.getGuildManager().getGuildByName(name).isDefined()){
                ChatUtil.sendMessage(sender, MessagesManager.error("Gildia o podanej nazwie juz istnieje!"));
                return;
            }
            if(!player.getWorld().equals(Bukkit.getWorlds().get(0))){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz stworzyc gildii w tym swiecie!"));
                return;
            }
            if(this.plugin.getGuildManager().getGuildByLocation(player.getLocation().getBlock().getLocation()).isDefined()){
                ChatUtil.sendMessage(sender, MessagesManager.error("W tym miejscu jest juz gildia!"));
                return;
            }
            if(this.plugin.getGuildManager().isNearSpawn(player.getLocation().getBlock().getLocation())){
                ChatUtil.sendMessage(sender, MessagesManager.error("Jestes zbyt blisko spawnu!"));
                return;
            }
            if(this.plugin.getGuildManager().isNearGuild(player.getLocation().getBlock().getLocation())){
                ChatUtil.sendMessage(sender, MessagesManager.error("Jestes zbyt blisko innej gildii!"));
                return;
            }
            if(this.plugin.getRegionManager().isOutOfBorder(player.getLocation().getBlock().getLocation())){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz stworzyc gildii poza borderem swiata!"));
                return;
            }
            if(this.plugin.getGuildManager().isNearBorder(player.getLocation().getBlock().getLocation())){
                ChatUtil.sendMessage(sender, MessagesManager.error("Jestes zbyt blisko granicy swiata!"));
                return;
            }
            if(!this.plugin.getGuildManager().hasItems(player) && !user.canByGroup(UserGroup.ADMIN)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Nie posiadasz przedmiotow potrzebnych do zalozenia gildii, sprawdzisz je pod /g itemy!"));
                return;
            }
            this.plugin.getGuildManager().removeItems(player);
            if(user.isOnline() && this.plugin.getProtectionManager().isProtected(user.getUuid())){
                this.plugin.getProtectionManager().removeUser(user.getUuid());
                TitleUtil.sendActionBar(player, ChatUtil.fixColor("&4Twoja ochrona wlasnie wygasla!"));
            }
            this.plugin.getGuildManager().createGuild(player, tag, name);
            String message = MessagesManager.guildCreateMessage;
            message = message.replace("{TAG}", tag);
            message = message.replace("{NAME}", name);
            message = message.replace("{OWNER}", player.getName());
            String finalMessage = message;
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isGuildMessages()).forEach(onlineUser -> ChatUtil.sendMessage(onlineUser.asPlayer(), finalMessage));
        });
        return true;
    }
}
