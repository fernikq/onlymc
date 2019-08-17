package pl.fernikq.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.fernikq.core.CorePlugin;

import java.io.File;
import java.lang.reflect.Field;

public class MessagesManager {

    private final CorePlugin plugin;
    private final File file;

    public static String commandErrorMessage;
    public static String commandErrorPermission;
    public static String commandCorrectUsage;
    public static String errorFormat;
    public static String teleportStartMessage;
    public static String teleportCancelMessage;
    public static String teleportFinishPlayerMessage;
    public static String teleportFinishLocationMessage;

    public MessagesManager(CorePlugin plugin){
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), "messages.yml");
        setDefaultValues();
    }

    public void reload(){
        load();
        save();
    }

    public void save(){
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for(Field field : MessagesManager.class.getFields()) {
                fileConfiguration.set("Messages." + field.getName(), field.get(null));
            }
            fileConfiguration.save(file);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void load(){
        try{
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for(Field field : MessagesManager.class.getFields()){
                if(fileConfiguration.isSet("Messages."+field.getName())){
                    field.set(null, fileConfiguration.get("Messages."+field.getName()));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static String error(String message){
        return errorFormat.replace("{ERROR}", message);
    }

    public static String usage(String message){
        return commandCorrectUsage.replace("{USAGE}", message);
    }

    private void setDefaultValues(){
        this.commandErrorMessage = "&4&lBlad &8>> &fBlad podczas wykonywania komendy, zglos sie do administratora!";
        this.commandErrorPermission = "&4&lBlad &8>> &fNie posiadasz wystarczajacych uprawnien aby uzyc tej komendy!";
        this.errorFormat = "&4&lBlad &8>> &f{ERROR}";
        this.commandCorrectUsage = "&c&lPoprawne uzycie&8: &f{USAGE}";
        this.teleportCancelMessage = "&4&lBlad &8>> &fPoruszyles sie, teleportacja anulowana!";
        this.teleportStartMessage = "&8>> {n}Zostaniesz przeteleportowany za {c}{TIME} sek.";
        this.teleportFinishLocationMessage = "&8>> {n}Zostales przeteleportowany na {c}{LOCATION}";
        this.teleportFinishPlayerMessage = "&8>> {n}Zostales przeteleportowany do gracza {c}{PLAYER}";
    }
}
