package pl.fernikq.core.command.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HelpopCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Cache<UUID, Long> cooldowns;

    public HelpopCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.cooldowns = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/helpop <wiadomosc>"));
        }
        String message = StringUtils.join(args, " ", 0, args.length);
        if(cooldowns.asMap().containsKey(player.getUniqueId())){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Poczekaj chwile przed ponownym napisaniem &8[&7"+ TimeUtil.getTimeToString(cooldowns.asMap().get(player.getUniqueId()) - System.currentTimeMillis())+"&8]"));
        }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 60000);
        ChatUtil.sendMessage(sender, "&8>> {n}Wyslales wiadomosc do {c}administracji&8!");
        Bukkit.getOnlinePlayers().forEach(online -> {
            this.plugin.getUserManager().getUser(online.getUniqueId()).filter(user -> user.canByGroup(UserGroup.HELPER))
                    .peek(user -> {
                        user.asPlayer().sendMessage(ChatUtil.fixColor(MessagesManager.helpopFormat.replace("{NICK}", sender.getName()))+message);
                    });
        });
        return true;
    }
}
