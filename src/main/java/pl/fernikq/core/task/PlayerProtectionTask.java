package pl.fernikq.core.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.fernikq.core.util.TitleUtil;

import java.util.Objects;

public class PlayerProtectionTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public PlayerProtectionTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getProtectionManager().getProtectedUsers().forEach(protectedUser -> {
            Player player = plugin.getServer().getPlayer(protectedUser.getUuid());
            if(Objects.isNull(player) || !player.isOnline()){
                return;
            }
            protectedUser.setSeconds(protectedUser.getSeconds() - 1);
            if(protectedUser.getSeconds() <= 0){
                this.plugin.getProtectionManager().removeUser(protectedUser.getUuid());
                this.plugin.getTagManager().updateTag(player);
                TitleUtil.sendActionBar(player, ChatUtil.fixColor("&4Twoja ochrona wlasnie wygasla!"));
                return;
            }
            TitleUtil.sendActionBar(player, ChatUtil.fixColor("&ePozostaly czas ochrony: &6"+ TimeUtil.getTimeFromSeconds(protectedUser.getSeconds())));
        });
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void stop() {
        cancel();
    }
}
