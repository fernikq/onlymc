package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class SignChangeListener implements Listener {

    private final CorePlugin plugin;

    public SignChangeListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        this.plugin.getUserManager().getUser(player.getUniqueId()).filter(user -> user.canByGroup(UserGroup.ADMIN))
                .peek(user -> {
                    for (int i = 0; i < event.getLines().length; i++) {
                        event.setLine(i, ChatUtil.fixColor(event.getLine(i)));
                    }
                });
    }
}
