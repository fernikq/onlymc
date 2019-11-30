package pl.fernikq.core.command.simpleCommand;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.user.UserGroup;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleCommandManager {

    private final CorePlugin plugin;
    private File commandFile;
    private Set<CustomCommand> customCommands;
    private Set<String> blockedCommands;
    private Set<String> blockedCommandsInGuild;
    private Set<String> allowedDuringPVP;
    private String helpCommandMessage;

    public SimpleCommandManager(CorePlugin plugin){
        this.plugin = plugin;
        this.customCommands = new HashSet<>();
        checkFile();
        loadCommands();
        loadBlockedCommands();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        commandFile = new File(this.plugin.getDataFolder(), "commands.yml");
        if(!commandFile.exists()){
            this.plugin.saveResource("commands.yml", true);
        }
    }

    public void reload(){
        checkFile();
        loadBlockedCommands();
    }

    public void loadBlockedCommands(){
        this.blockedCommands = new HashSet<>(getCommandFile().getStringList("BlockedCommands"));
        this.blockedCommandsInGuild = new HashSet<>(getCommandFile().getStringList("BlockedCommandsInGuild"));
        this.allowedDuringPVP = new HashSet<>(getCommandFile().getStringList("AllowedDuringPVP"));
        this.helpCommandMessage = getCommandFile().getString("HelpCommandMessage");
    }

    public void loadCommands(){
        ConfigurationSection configurationSection = getCommandFile().getConfigurationSection("Commands");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection c = configurationSection.getConfigurationSection(s);
            String name = c.getString("name");
            List<String> aliases = c.getStringList("aliases");
            UserGroup group = UserGroup.getByName(c.getString("group"));
            if(group == null){
                group = UserGroup.PLAYER;
            }
            List<String> feedback = c.getStringList("feedback");
            SimpleCommand simpleCommand = new SimpleCommand(name, aliases, group, feedback);
            this.customCommands.add(new SimpleCustomCommand(name, aliases, group, this.plugin, simpleCommand));
        }
        customCommands.forEach(customCommand -> customCommand.register());
    }

    public YamlConfiguration getCommandFile() {
        return YamlConfiguration.loadConfiguration(commandFile);
    }

    public Set<String> getBlockedCommands() {
        return new HashSet<>(this.blockedCommands);
    }

    public Set<String> getBlockedCommandsInGuild() {
        return new HashSet<>(this.blockedCommandsInGuild);
    }

    public Set<String> getAllowedDuringPVP() {
        return new HashSet<>(this.allowedDuringPVP);
    }

    public String getHelpCommandMessage() {
        return helpCommandMessage;
    }
}
