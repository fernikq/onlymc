package pl.fernikq.core.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

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

    public static int teleportHomeTime;
    public static int teleportWarpTime;
    public static int teleportSpawnTime;
    public static int autoMessageTime;
    public static boolean chatEnabled;
    public static String spawnLocation;
    public static List<String> blockedCommands;

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
        teleportHomeTime = 10;
        teleportWarpTime = 10;
        teleportSpawnTime = 10;
        autoMessageTime = 60;
        chatEnabled = true;
        spawnLocation = LocationUtil.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation());
        blockedCommands = Arrays.asList("/bukkit:me", "/minecraft:me", "/me");
    }
}
