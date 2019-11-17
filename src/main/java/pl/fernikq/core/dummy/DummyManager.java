package pl.fernikq.core.dummy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class DummyManager {

    private CorePlugin plugin;

    public DummyManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public void createScore(User user) {
        user.getDummy().createScore(plugin);
    }

    public void updateScore(User user) {
        Bukkit.getOnlinePlayers().forEach(online -> {
           this.plugin.getUserManager().getUser(online.getUniqueId()).peek(onlineUser -> {
                onlineUser.getDummy().updateScore(user, plugin);
           });
        });
    }
}
