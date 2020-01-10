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
    public static int teleportGuildTime;
    public static int teleportTpaTime;
    public static int autoMessageTime;
    public static boolean chatEnabled;
    public static String spawnLocation;
    public static boolean limitInventoryClicks;
    public static long stoneGeneratorRegenerationTime;
    public static long turboDropTime;
    public static long turboExpTime;
    public static double turboDropMultiplier;
    public static double turboExpMultiplier;
    public static int tntExplodeBelow;
    public static String tntHours;
    public static int maxGoldenApplesInInventory;
    public static int maxEnchantedGoldenApplesInInventory;
    public static int maxPearlsInInventory;
    public static int dropStoneExp;
    public static int dropObsidianExp;
    public static double coinsDropFromStoneChance;
    public static String coinsDropFromStoneAmount;
    public static int playerStartPoints;
    public static int maxAmountOfPointsByKilling;
    public static String guildAttackProtectionAfterCreate;
    public static String guildExplosionProtectionAfterCreate;
    public static String guildExpireAfterCreateTime;
    public static String guildNextAttackAfterAttack;
    public static int guildMaxStartMembersSize;
    public static int guildMaxStartAlliesSize;
    public static int guildStartHealth;
    public static int guildStartCuboidSize;
    public static int guildCuboidSizeAddByEnlarge;
    public static int minimalDistanceBetweenGuilds;
    public static int minimalDistanceFromSpawn;
    public static int minimalDistanceFromBorder;
    public static int guildCenterY;
    public static List<Integer> guildAlliesSizeEnlargeCost;
    public static List<Integer> guildMembersSizeEnlargeCost;
    public static List<Integer> guildCuboidSizeEnlargeCost;
    public static int guildTreasureSizeFirstLevelCost;
    public static int guildTreasureSizeSecondLevelCost;
    public static int guildTreasureSizeThirdLevelCost;
    public static int enderchestSizeFirstLevelCost;
    public static int enderchestSizeSecondLevelCost;
    public static int enderchestSizeThirdLevelCost;
    public static String guildMaxTimeRenew;
    public static int guildTimeRenewCost;
    public static int playerFightTime;
    public static int blockBuildingBelowYDuringFight;
    public static int playerPointsResetCost;
    public static double primedTNTSpeed;
    public static double punchingLinePower;
    public static String guildDenyBuildTimeAfterExplosion;
    public static List<String> guildPlayerItemsToCreate;
    public static List<String> guildVipItemsToCreate;
    public static boolean usePremiumHeadsInGUI;
    public static boolean blockOpeningEnderchestDuringFight;
    public static boolean isServerRunningInOnlineModeOrHasPremiumLogin;

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
        teleportTpaTime = 10;
        teleportGuildTime = 10;
        autoMessageTime = 60;
        chatEnabled = true;
        spawnLocation = LocationUtil.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation());
        limitInventoryClicks = false;
        stoneGeneratorRegenerationTime = 2000;
        tntExplodeBelow = 50;
        tntHours = "10-24";
        maxGoldenApplesInInventory = 8;
        maxEnchantedGoldenApplesInInventory = 2;
        maxPearlsInInventory = 4;
        coinsDropFromStoneChance = 0.4;
        coinsDropFromStoneAmount = "1-5";
        dropStoneExp = 10;
        dropObsidianExp = 20;
        turboDropTime = 0L;
        turboExpTime = 0L;
        turboDropMultiplier = 1.5;
        turboExpMultiplier = 2.0;
        playerStartPoints = 500;
        maxAmountOfPointsByKilling = 300;
        guildExplosionProtectionAfterCreate = "1d";
        guildAttackProtectionAfterCreate = "1d";
        guildExpireAfterCreateTime = "7d";
        guildNextAttackAfterAttack = "12h";
        guildMaxStartAlliesSize = 4;
        guildMaxStartMembersSize = 12;
        guildMembersSizeEnlargeCost = Arrays.asList(150, 300, 450, 600, 750, 900, 1050, 1300);
        guildAlliesSizeEnlargeCost = Arrays.asList(800, 1600, 2400, 3200);
        guildTreasureSizeFirstLevelCost = 1000;
        guildTreasureSizeSecondLevelCost = 2000;
        guildTreasureSizeThirdLevelCost = 3000;
        enderchestSizeFirstLevelCost = 500;
        enderchestSizeSecondLevelCost = 1500;
        enderchestSizeThirdLevelCost = 2500;
        guildStartHealth = 3;
        guildCuboidSizeAddByEnlarge = 5;
        guildStartCuboidSize = 50;
        guildCuboidSizeEnlargeCost = Arrays.asList(200, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000);
        minimalDistanceBetweenGuilds = 50;
        minimalDistanceFromSpawn = 400;
        guildCenterY = 40;
        guildPlayerItemsToCreate = Arrays.asList("diamond:0:64:Diamenty",
                "emerald:0:64:Emeraldy",
                "gold_ingot:0:64:Zloto",
                "obsidian:0:64:Obsydian",
                "raw_fish:0:32:Ryby",
                "bread:0:32:Chleb",
                "anvil:0:32:Kowadla",
                "book:0:32:Ksiazki",
                "bookshelf:0:64:Biblioteczki");
        guildVipItemsToCreate = Arrays.asList("diamond:0:32:Diamenty",
                "emerald:0:32:Emeraldy",
                "gold_ingot:0:32:Zloto",
                "obsidian:0:32:Obsydian",
                "raw_fish:0:16:Ryby",
                "bread:0:16:Chleb",
                "anvil:0:16:Kowadla",
                "book:0:16:Ksiazki",
                "bookshelf:0:32:Biblioteczki");
        isServerRunningInOnlineModeOrHasPremiumLogin = false;
        usePremiumHeadsInGUI = true;
        guildMaxTimeRenew = "7d";
        guildTimeRenewCost = 2500;
        guildDenyBuildTimeAfterExplosion = "1m";
        playerFightTime = 30;
        punchingLinePower = -1.2;
        blockBuildingBelowYDuringFight = 50;
        blockOpeningEnderchestDuringFight = true;
        playerPointsResetCost = 500;
        minimalDistanceFromBorder = 120;
        primedTNTSpeed = 1.2;
    }
}
