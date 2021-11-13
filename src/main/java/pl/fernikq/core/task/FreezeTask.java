package pl.fernikq.core.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.fernikq.core.util.TitleUtil;

public class FreezeTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public FreezeTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(ConfigManager.freezeTime > System.currentTimeMillis()){
            this.plugin.getUserManager().getUsers().forEach(user -> {
                if(user.canByGroup(UserGroup.TEST_HELPER)) return;
                Player player = user.asPlayer();
                TitleUtil.sendTitle(player, ChatUtil.fixColor("&8[&b&lZamrozenie&8]"), 2);
                TitleUtil.sendSubTitle(player, ChatUtil.fixColor("&fOczekiwanie na start edycji..."), 2);
            });
        }
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 40, 60);
    }

    @Override
    public void stop() {
        cancel();
    }
}
