package pl.fernikq.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;

public class GuildExpireCheckTask extends BukkitRunnable implements SimpleTask {

    private final CorePlugin plugin;

    public GuildExpireCheckTask(CorePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void start() {
        runTaskTimer(plugin, 3600, 3600);
    }

    @Override
    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        this.plugin.getGuildManager().getGuilds().filter(guild -> guild.getExpireTime() < System.currentTimeMillis()).forEach(guild -> {
            this.plugin.getGuildManager().deleteGuild(guild);
            String message = MessagesManager.guildExpireMessage;
            message = message.replace("{TAG}", guild.getTag());
            message = message.replace("{NAME}", guild.getName());
            message = message.replace("{X}", Integer.toString(guild.getRegion().getCenter().getBlockX()));
            message = message.replace("{Y}", Integer.toString(guild.getRegion().getCenter().getBlockY()));
            message = message.replace("{Z}", Integer.toString(guild.getRegion().getCenter().getBlockZ()));
            String finalMessage = message;
            this.plugin.getUserManager().getOnlineUsers().stream().filter(user -> user.getUserChat().isGuildMessages()).forEach(user -> ChatUtil.sendMessage(user.asPlayer(), finalMessage));
        });
    }
}
