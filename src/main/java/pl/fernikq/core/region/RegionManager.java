package pl.fernikq.core.region;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegionManager {

    private final CorePlugin plugin;
    private List<Region> regions;
    private File regionFile;
    private boolean regionsEnabled;

    private Comparator<Region> regionComparator;

    public RegionManager(CorePlugin plugin){
        this.plugin = plugin;
        this.regions = new ArrayList<>();
        this.regionComparator = new Comparator<Region>() {
            @Override
            public int compare(Region o1, Region o2) {
                int i = Integer.compare(o2.getPriority(), o1.getPriority());
                if(i == 0){
                    if(o1.getRegionName() == null){
                        return -1;
                    }
                    if(o2.getRegionName() == null){
                        return 1;
                    }
                    i = o1.getRegionName().compareTo(o2.getRegionName());
                }
                return i;
            }
        };
        checkFile();
        loadRegions();
    }

    public void reload(){
        checkFile();
        loadRegions();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        regionFile = new File(this.plugin.getDataFolder(), "regions.yml");
        if(!regionFile.exists()){
            this.plugin.saveResource("regions.yml", true);
        }
    }

    public void loadRegions(){
        regions.clear();
        regionsEnabled = getRegionFile().getBoolean("regionsEnabled");
        if(!regionsEnabled){
            return;
        }
        ConfigurationSection configurationSection = getRegionFile().getConfigurationSection("Regions");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection c = configurationSection.getConfigurationSection(s);
            Location lowerCorner = new Location(Bukkit.getWorld(c.getString("location.world")), c.getInt("location.lower.x"),  c.getInt("location.lower.y"), c.getInt("location.lower.z"));
            Location upperCorner = new Location(Bukkit.getWorld(c.getString("location.world")), c.getInt("location.upper.x"),  c.getInt("location.upper.y"), c.getInt("location.upper.z"));
            List<String> blockedCommands = c.getStringList("blockedCommands");
            Region region = new Region().setRegionName(c.getString("name")).setLowerCorner(lowerCorner).setUpperCorner(upperCorner).
                    setPriority(c.getInt("priority")).setCanBuild(c.getBoolean("canBuild")).
                    setCanDestroy(c.getBoolean("canDestroy")).setCanThrowPearls(c.getBoolean("canThrowPearls")).
                    setCanUseBuckets(c.getBoolean("canUseBuckets")).setCanExplode(c.getBoolean("canExplode")).
                    setCanHurt(c.getBoolean("canHurt")).setCanSpawnVehicles(c.getBoolean("canSpawnVehicles")).
                    setStoneGeneratorRegion(c.getBoolean("isStoneGeneratorRegion")).setAllowFireSpread(c.getBoolean("allowFireSpread")).
                    setCanSpreadFire(c.getBoolean("canSpreadFire")).setAllowMobSpawning(c.getBoolean("allowMobSpawning")).
                    setCanEnterDuringFight(c.getBoolean("canEnterDuringFight")).setCanChangePaintings(c.getBoolean("canChangePaintings")).
                    setCanChangeFrames(c.getBoolean("canChangeFrames")).setCanDestroyFarmland(c.getBoolean("canDestroyFarmlands")).
                    setAllowLeavesDecay(c.getBoolean("allowLeavesDecay")).setBlockedCommands(blockedCommands);
            this.regions.add(region);
        }
    }

    public List<Region> getRegionsByLocation(Location location){
        List<Region> regions = getRegions().filter(region -> region.isIn(location)).collect(Collectors.toList());
        regions.sort(this.regionComparator);
        return regions;
    }

    public Region getRegionByLocation(Location location){
        if(getRegionsByLocation(location).isEmpty()){
            return null;
        }
        return getRegionsByLocation(location).get(0);
    }

    public boolean isRegionsEnabled() {
        return regionsEnabled;
    }

    public YamlConfiguration getRegionFile(){
        return YamlConfiguration.loadConfiguration(regionFile);
    }

    public Set<Region> getRegions(){
        return HashSet.ofAll(this.regions);
    }

    public RegionFeedback can(Location location, RegionProtectionType type){
        switch(type){
            case EXPLOSION:{
                if(location.getBlockY() >= ConfigManager.tntExplodeBelow){
                    return RegionFeedback.DENY;
                }
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanExplode() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case IGNITE_TNT:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanExplode() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case FIRE_SPREAD:{
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isAllowFireSpread() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case MOB_SPAWNING:{
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isAllowMobSpawning() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case FRAMES:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case PAINTINGS:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case LEAVES:{
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isAllowLeavesDecay() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case FARMLANDS:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanDestroyFarmland() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case HURT:{
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanHurt() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case CAN_FIRE_SPREAD:{
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanSpreadFire() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
        }
        System.out.println("can without player switch failed");
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback can(User user, Location location, RegionProtectionType type){
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        switch(type){
            case CAN_FIRE_SPREAD:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanSpreadFire() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case FRAMES:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case PAINTINGS:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                return RegionFeedback.ALLOW;
            }
            case BUILD:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanBuild() ? RegionFeedback.ALLOW : RegionFeedback.DENY_BUILD_SPAWN;
                }
                return RegionFeedback.ALLOW;
            }
            case DESTROY:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    if(region.isCanDestroy()){
                        if(region.isStoneGeneratorRegion()){
                            if(user.asPlayer().getItemInHand() != null && user.asPlayer().getItemInHand().getType() == Material.GOLD_PICKAXE){
                                return RegionFeedback.DENY_DESTROY_GOLD_PICKAXE;
                            }
                        }
                        return RegionFeedback.ALLOW;
                    }
                    return RegionFeedback.DENY_DESTROY_SPAWN;
                }
                return RegionFeedback.ALLOW;
            }
            case BUCKETS:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanUseBuckets() ? RegionFeedback.ALLOW : RegionFeedback.DENY_BUCKETS;
                }
                return RegionFeedback.ALLOW;
            }
            case PEARLS:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanThrowPearls() ? RegionFeedback.ALLOW : RegionFeedback.DENY_PEARLS;
                }
                return RegionFeedback.ALLOW;
            }
            case VEHICLES:{
                //TODO Guilds
                if(checkRegions() != null){
                    return checkRegions();
                }
                for(Region region : getRegionsByLocation(location)){
                    return region.isCanSpawnVehicles() ? RegionFeedback.ALLOW : RegionFeedback.DENY_SPAWN_VEHICLES;
                }
                return RegionFeedback.ALLOW;
            }
        }
        System.out.println("can with player switch failed");
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canHurt(User damager, User victim){
        //TODO Relations
        if(checkRegions() != null){
            return checkRegions();
        }
        Region damagerRegion = getRegionByLocation(damager.asPlayer().getLocation().getBlock().getLocation());
        Region victimRegion = getRegionByLocation(victim.asPlayer().getLocation().getBlock().getLocation());
        if(damagerRegion == null && victimRegion == null){
            return RegionFeedback.ALLOW;
        }
        if(damagerRegion == null){
            if(victimRegion.isCanHurt()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY;
            }
        }
        if(victimRegion == null){
            if(damagerRegion.isCanHurt()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_PVP_OTHER_REGION;
            }
        }
        if(!victimRegion.isCanHurt() && !damagerRegion.isCanHurt()){
            return RegionFeedback.DENY;
        }
        if(damagerRegion.isCanHurt() && !victimRegion.isCanHurt()){
            return RegionFeedback.DENY;
        }
        if(!damagerRegion.isCanHurt() && victimRegion.isCanHurt()){
            return RegionFeedback.DENY_PVP_OTHER_REGION;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canProccessCommand(User user, Location location, String command){
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        if(this.plugin.getSimpleCommandManager().getBlockedCommands().contains(command.toLowerCase())){
            return RegionFeedback.DENY_PROCCESS_COMMAND;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            java.util.Set<String> blockedCommands = new java.util.HashSet<>();
            region.getBlockedCommands().forEach(blocked -> blockedCommands.add(blocked.toLowerCase()));
            if(blockedCommands.contains(command.toLowerCase())) {
                return RegionFeedback.DENY_PROCCESS_COMMAND_REGION;
            }
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback checkRegions(){
        if(!this.regionsEnabled){
            return RegionFeedback.ALLOW;
        }
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        return null;
    }
}
