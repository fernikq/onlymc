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
        if(!this.regionsEnabled){
            return RegionFeedback.ALLOW;
        }
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        for(Region region : getRegionsByLocation(location)){
            switch(type){
                case EXPLOSION:{
                    return region.isCanExplode() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case FIRE_SPREAD:{
                    return region.isAllowFireSpread() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case MOB_SPAWNING:{
                    return region.isAllowMobSpawning() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case FRAMES:{
                    return region.isCanChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case PAINTINGS:{
                    return region.isCanChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case LEAVES:{
                    return region.isAllowLeavesDecay() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case FARMLANDS:{
                    return region.isCanDestroyFarmland() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case HURT:{
                    return region.isCanHurt() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case CAN_FIRE_SPREAD:{
                    return region.isCanSpreadFire() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
            }
            System.out.println("can without player switch failed");
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback can(Player player, Location location, RegionProtectionType type){
        if(!this.regionsEnabled){
            return RegionFeedback.ALLOW;
        }
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        for(Region region : getRegionsByLocation(location)){
            switch(type){
                case CAN_FIRE_SPREAD:{
                    return region.isCanSpreadFire() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case FRAMES:{
                    return region.isCanChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case PAINTINGS:{
                    return region.isCanChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
                }
                case BUILD:{
                    return region.isCanBuild() ? RegionFeedback.ALLOW : RegionFeedback.DENY_BUILD_SPAWN;
                }
                case DESTROY:{
                    if(region.isCanDestroy()){
                        if(region.isStoneGeneratorRegion()){
                            if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_PICKAXE){
                                return RegionFeedback.DENY_DESTROY_GOLD_PICKAXE;
                            }
                        }
                        return RegionFeedback.ALLOW;
                    }
                    return RegionFeedback.DENY_DESTROY_SPAWN;
                }
                case BUCKETS:{
                    return region.isCanUseBuckets() ? RegionFeedback.ALLOW : RegionFeedback.DENY_BUCKETS;
                }
                case PEARLS:{
                    return region.isCanThrowPearls() ? RegionFeedback.ALLOW : RegionFeedback.DENY_PEARLS;
                }
                case VEHICLES:{
                    return region.isCanSpawnVehicles() ? RegionFeedback.ALLOW : RegionFeedback.DENY_SPAWN_VEHICLES;
                }
            }
            System.out.println("can with player switch failed");
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canHurt(Player damager, Player victim){
        if(!this.regionsEnabled){
            return RegionFeedback.ALLOW;
        }
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        Region damagerRegion = getRegionByLocation(damager.getLocation().getBlock().getLocation());
        Region victimRegion = getRegionByLocation(victim.getLocation().getBlock().getLocation());
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
        //TODO Relations
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canProccessCommand(Player player, Location location, String command){
        if(!this.regionsEnabled){
            return RegionFeedback.ALLOW;
        }
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        for(Region region : getRegionsByLocation(location)){
            java.util.Set<String> blockedCommands = new java.util.HashSet<>();
            region.getBlockedCommands().forEach(blocked -> blockedCommands.add(blocked.toLowerCase()));
            if(blockedCommands.contains(command.toLowerCase())){
                return RegionFeedback.DENY_PROCCESS_COMMAND;
            }else{
                return RegionFeedback.ALLOW;
            }
        }
        return RegionFeedback.ALLOW;
    }
}
