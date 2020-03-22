package pl.fernikq.core.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.discord.listener.RewardListener;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private final CorePlugin plugin;
    private JDA jda;

    public DiscordManager(CorePlugin plugin){
        this.plugin = plugin;
        initDiscordBot();
    }

    private void initDiscordBot(){
        if(!ConfigManager.discordEnableDiscordBot){
            return;
        }
        JDABuilder jdaBuilder = new JDABuilder();
        jdaBuilder.setToken(ConfigManager.discordBotToken);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setActivity(Activity.watching("Ciebie"));
        jdaBuilder.addEventListeners(new RewardListener(this.plugin));
        try {
            this.jda = jdaBuilder.build();
        } catch(LoginException e) {
            e.printStackTrace();
        }
    }

    public JDA getJda() {
        return jda;
    }
}
