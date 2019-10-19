package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class PlayerLoginListener implements Listener {

    private final CorePlugin plugin;

    public PlayerLoginListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        if(!this.plugin.getUserManager().isCorrect(player.getName())){
            User user = this.plugin.getUserManager().getUser(player.getName()).get();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatUtil.fixColor("{n}Twoj nick rozni sie od poprzedniego!\n{n}Zmien go na {c}"+user.getName()));
            return;
        }
    }
}
