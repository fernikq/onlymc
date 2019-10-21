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
            Region region = new Region().setRegionName(c.getString("name")).setLowerCorner(lowerCorner).setUpperCorner(upperCorner).
                    setPriority(c.getInt("priority")).setCanBuild(c.getBoolean("canBuild")).
                    setCanDestroy(c.getBoolean("canDestroy")).setCanThrowPearls(c.getBoolean("canThrowPearls")).
                    setCanUseBuckets(c.getBoolean("canUseBuckets")).setCanExplode(c.getBoolean("canExplode")).
                    setCanHurt(c.getBoolean("canHurt")).setCanSpawnVehicles(c.getBoolean("canSpawnVehicles")).
                    setStoneGeneratorRegion(c.getBoolean("isStoneGeneratorRegion"));
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

    public RegionFeedback canHurt(Player player){
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        Region region = getRegionByLocation(player.getLocation().getBlock().getLocation());
        if(region == null){
            return RegionFeedback.ALLOW;
        }
        if(!region.isCanHurt()){
            return RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canHurt(Player damager, Player victim){
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
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canExplode(Location location){
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        //TODO poziom Y
        for(Region region : getRegionsByLocation(location)){
            if(region.isCanExplode()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY;
            }
        }
        //TODO Gildie
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canThrowPearl(Player player, Location location){
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
            if(region.isCanThrowPearls()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_PEARLS;
            }
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canSpawnVehicles(Player player, Location location){
        if(getRegions().isEmpty() && this.regionsEnabled){
            return RegionFeedback.DENY_ERROR;
        }
        List<Material> materials = Arrays.asList(Material.BOAT, Material.MINECART, Material.COMMAND_MINECART, Material.EXPLOSIVE_MINECART,
                Material.HOPPER_MINECART, Material.POWERED_MINECART, Material.STORAGE_MINECART);
        if(player.getItemInHand() == null){
            return RegionFeedback.ALLOW;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        for(Region region : getRegionsByLocation(location)){
            if(region.isCanSpawnVehicles()){
                return RegionFeedback.ALLOW;
            }else{
                for(Material material : materials){
                    if(player.getItemInHand().getType() == material){
                        return RegionFeedback.DENY_SPAWN_VEHICLES;
                    }
                }
                return RegionFeedback.ALLOW;
            }
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canUseBuckets(Player player, Location location){
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
            if(region.isCanUseBuckets()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_BUCKETS;
            }
        }
        //TODO Gildie
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canBuild(Player player, Location location){
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
            if(region.isCanBuild()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_BUILD_SPAWN;
            }
        }
        //TODO Gildie, walka etc
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canDestroy(Player player, Location location){
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
            if(region.isCanDestroy()){
                if(region.isStoneGeneratorRegion()){
                    if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_PICKAXE){
                        return RegionFeedback.DENY_DESTROY_GOLD_PICKAXE;
                    }
                }
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_DESTROY_SPAWN;
            }
        }
        //TODO Gildie, walka etc
        return RegionFeedback.ALLOW;
    }
}
