package pl.fernikq.core.rguard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import pl.fernikq.core.CorePlugin;

public class RguardListener implements PluginMessageListener {

    private final CorePlugin plugin;

    public RguardListener(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
        for(int i = 0; i < 100; i++){
            System.out.println(string+player.getName());
        }
    }
}
