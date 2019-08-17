package pl.fernikq.core.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import pl.fernikq.core.CorePlugin;

import java.lang.reflect.Field;

public class ConfigManager {

    private final CorePlugin plugin;

    public ConfigManager(CorePlugin plugin){
        this.plugin = plugin;
        setValues();
    }

    //MYSQL
    public static String mysqlHost;
    public static String mysqlBase;
    public static String mysqlUser;
    public static String mysqlPassword;
    public static int mysqlPort;

    public void load(){
        try{
            this.plugin.saveDefaultConfig();
            FileConfiguration fileConfiguration = this.plugin.getConfig();
            for(Field field : ConfigManager.class.getFields()){
                if(fileConfiguration.isSet("Config."+field.getName())){
                    field.set(null, fileConfiguration.get("Config."+field.getName()));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void save(){
        try{
            FileConfiguration fileConfiguration = this.plugin.getConfig();
            for(Field field : ConfigManager.class.getFields()){
                fileConfiguration.set("Config."+field.getName(), field.get(null));
            }
            this.plugin.saveConfig();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void reload(){
        this.plugin.reloadConfig();
        load();
        save();
    }

    private void setValues(){
        mysqlHost = "localhost";
        mysqlBase = "onlymc";
        mysqlUser = "root";
        mysqlPassword = "password";
        mysqlPort = 3306;
    }
}
