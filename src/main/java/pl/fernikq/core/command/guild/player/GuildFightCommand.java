package pl.fernikq.core.command.guild.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.region.GuildRegion;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.concurrent.TimeUnit;

public class GuildFightCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Cache<Guild, Long> cache;

    public GuildFightCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(!user.hasGuild()){
                ChatUtil.sendMessage(player, MessagesManager.error("Musisz posiadac gildie, aby uzyc tej komendy!"));
                return;
            }
            Guild guild = user.getGuild();
            if(this.cache.asMap().containsKey(guild)){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia juz wyzywala do walki, odczekaj chwile!"));
                return;
            }
            this.cache.put(guild, 1L);
            GuildRegion region = guild.getRegion();
            String message = "&8[&c&lWalka&8] &fGildia &c"+guild.getTag()+" &fchce walczyc! Ich koordynaty&8: &fX&8: &c"+region.getCenter().getBlockX()+" &fZ&8: &c"+region.getCenter().getBlockZ();
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isGuildMessages()).forEach(onlineUser -> ChatUtil.sendMessage(onlineUser.asPlayer(), message));
        });
        return true;
    }
}
