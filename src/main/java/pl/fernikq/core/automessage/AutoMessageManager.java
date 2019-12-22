package pl.fernikq.core.automessage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoMessageManager {

    private final CorePlugin plugin;
    private List<AutoMessage> autoMessages;
    private ScheduledExecutorService executorService;
    private File autoMessageFile;
    private int repeat;

    public AutoMessageManager(CorePlugin plugin){
        this.plugin = plugin;
        this.autoMessages = new ArrayList<>();
        checkFile();
        loadAutoMessages();
        start();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        autoMessageFile = new File(this.plugin.getDataFolder(), "automessage.yml");
        if(!autoMessageFile.exists()){
            this.plugin.saveResource("automessage.yml", true);
        }
    }

    public void reload(){
        stop();
        this.autoMessages.clear();
        checkFile();
        loadAutoMessages();
        start();
    }

    public void start(){
        repeat = 0;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(() -> {
            if(this.autoMessages.isEmpty()){
                return;
            }
            AutoMessage autoMessage = this.autoMessages.get(repeat);
            this.plugin.getUserManager().getOnlineUsers().stream().filter(user -> user.getUserChat().isAutoMessages()).forEach(user -> {
                for(String line : autoMessage.getLines()){
                    ChatUtil.sendMessage(user.asPlayer(), line);
                }
            });
            repeat++;
            if(repeat == this.autoMessages.size()){
                repeat = 0;
            }
        }, ConfigManager.autoMessageTime, ConfigManager.autoMessageTime, TimeUnit.SECONDS);
    }

    public void stop(){
        this.executorService.shutdown();
        repeat = 0;
    }

    public void loadAutoMessages(){
        ConfigurationSection configurationSection = getAutoMessageFile().getConfigurationSection("Automessage");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection c = configurationSection.getConfigurationSection(s);
            this.autoMessages.add(new AutoMessage(c.getStringList("list")));
        }
    }

    public YamlConfiguration getAutoMessageFile() {
        return YamlConfiguration.loadConfiguration(autoMessageFile);
    }

    public Set<AutoMessage> getAutoMessages() {
        return new HashSet<>(autoMessages);
    }
}
