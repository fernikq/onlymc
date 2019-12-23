package pl.fernikq.core.command.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.concurrent.TimeUnit;

public class ResetPointsCommand extends CustomCommand {

    private final CorePlugin plugin;
    private Cache<User, Long> cache;

    public ResetPointsCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(user.getUserStat().getCoins() < ConfigManager.playerPointsResetCost){
               ChatUtil.sendMessage(player, "&8>> {n}Aby zresetowac punkty potrzebujesz {c}"+ConfigManager.playerPointsResetCost+" {n}monet!");
               return;
           }
           if(user.getUserStat().getPoints() == ConfigManager.playerStartPoints){
               ChatUtil.sendMessage(player, MessagesManager.error("Twoje punkty sa rowne poczatkowej wartosci!"));
               return;
           }
           if(this.cache.asMap().containsKey(user)){
               user.getUserStat().setPoints(ConfigManager.playerStartPoints);
               ChatUtil.sendMessage(player, "&8>> {n}Twoje punkty zostaly zresetowane do {c}"+ConfigManager.playerStartPoints);
               this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_POINTS).sort());
               return;
           }
           this.cache.put(user, 100L);
           ChatUtil.sendMessage(player, "&8>> {n}Wpisz ponownie {c}/resetuj {n}aby potwierdzic reset punktow!");
           return;
        });
        return true;
    }
}
