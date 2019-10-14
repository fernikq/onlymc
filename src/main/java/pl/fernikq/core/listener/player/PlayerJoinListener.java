package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class PlayerJoinListener implements Listener {

    private final CorePlugin plugin;

    public PlayerJoinListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        User user = null;
        if(!this.plugin.getUserManager().isCorrect(player.getName())){
            user = this.plugin.getUserManager().getUser(player.getName()).get();
            if(Bukkit.getPlayerExact(player.getName()) != null){
                player.kickPlayer(ChatUtil.fixColor("{n}Twoj nick rozni sie od poprzedniego!\n{n}Zmien go na {c}"+user.getName()));
            }
            return;
        }
        user = this.plugin.getUserManager().getUser(player);
        if(!user.getName().equals(player.getName())){
            user.setName(player.getName());
            this.plugin.getUserManager().getUserData().updateUser(user);
        }
        user.setLastAddress(player.getAddress().getAddress().getHostAddress());
        this.plugin.getTagManager().createTag(player);
    }
}

