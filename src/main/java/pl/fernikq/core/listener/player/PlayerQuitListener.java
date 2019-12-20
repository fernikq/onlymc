package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class PlayerQuitListener implements Listener {

    private final CorePlugin plugin;

    public PlayerQuitListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage(ChatUtil.fixColor(MessagesManager.playerQuitMessage.replace("{PLAYER}", player.getName())));
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.getUserFight().isDuringFight()){
                player.setHealth(0.0);
                this.plugin.getFightManager().removeFight(user);
                String message = MessagesManager.playerFightLogoutMessage;
                message = message.replace("{PLAYER}", user.getName());
                String finalMessage = message;
                Bukkit.getOnlinePlayers().forEach(o -> ChatUtil.sendMessage(o, finalMessage));
            }
            this.plugin.getUserManager().updateUser(user);
            user.setScoreboard(null);
        });
        this.plugin.getTagManager().removeTag(player);
        this.plugin.getVanishManager().removeVanished(player);
    }
}
