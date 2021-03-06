package pl.fernikq.core.command.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TitleUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProtectionCommand extends CustomCommand {

    private final CorePlugin plugin;
    private final Cache<UUID, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    public ProtectionCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(!this.plugin.getProtectionManager().isProtected(player.getUniqueId())){
            ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz juz ochrony!"));
            return true;
        }
        if(this.cache.asMap().containsKey(player.getUniqueId())){
            this.plugin.getProtectionManager().removeUser(player.getUniqueId());
            this.plugin.getTagManager().updateTag(player);
            TitleUtil.sendActionBar(player, ChatUtil.fixColor("&4Twoja ochrona wlasnie wygasla!"));
            ChatUtil.sendMessage(player, "&8>> &fPomyslnie wylaczyles &eochrone startowa!");
            this.cache.asMap().remove(player.getUniqueId());
            return true;
        }
        this.cache.put(player.getUniqueId(), 1L);
        ChatUtil.sendMessage(player, "&8>> &fAby wylaczyc &eochrone startowa &fwpisz ponownie &6/ochrona");
        return true;
    }
}
