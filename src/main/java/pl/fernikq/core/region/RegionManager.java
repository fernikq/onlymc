package pl.fernikq.core.region;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.check.PlayerCheckUtil;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.drill.GuildDrill;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.NumberUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RegionManager {

    private final CorePlugin plugin;
    private List<Region> regions;
    private File regionFile;
    private boolean regionsEnabled;
    private Map<String, WorldBorder> worldBorders;

    private Comparator<Region> regionComparator;

    public RegionManager(CorePlugin plugin){
        this.plugin = plugin;
        this.regions = new ArrayList<>();
        this.worldBorders = new HashMap<>();
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
                    setPriority(c.getInt("priority")).setBlockedCommands(blockedCommands).setCanPlayerBuild(c.getBoolean("player.canBuild")).setCanPlayerDestroy(c.getBoolean("player.canDestroy"))
                    .setCanPlayerThrowPearls(c.getBoolean("player.canThrowPearls")).setCanPlayerUserBuckets(c.getBoolean("player.canUseBuckets")).setCanPlayerHurtOther(c.getBoolean("player.canHurtOther"))
                    .setCanPlayerBeHurt(c.getBoolean("player.canBeHurt")).setCanPlayerSpawnVehicles(c.getBoolean("player.canSpawnVehicles")).setCanPlayerIgniteBlocks(c.getBoolean("player.canIgniteBlocks"))
                    .setCanPlayerIgniteTNT(c.getBoolean("player.canIgniteTNT")).setCanPlayerChangePaintings(c.getBoolean("player.canChangePaintings")).setCanPlayerChangeFrames(c.getBoolean("player.canChangeFrames"))
                    .setCanPlayerDestroyFarmlands(c.getBoolean("player.canDestroyFarmlands")).setCanPlayerBeHungry(c.getBoolean("player.canBeHungry")).setCanPlayerJoinDuringPVP(c.getBoolean("player.canJoinDuringPVP"))
                    .setCanEntityChangePaintings(c.getBoolean("entities.canChangePaintings")).setCanEntityChangeFrames(c.getBoolean("entities.canChangeFrames"))
                    .setCanEntityExplode(c.getBoolean("entities.canExplode")).setCanEntityIgniteTNT(c.getBoolean("entities.canIgniteTNT")).setStoneGeneratorRegion(c.getBoolean("other.isStoneGeneratorRegion"))
                    .setAllowFireSpread(c.getBoolean("other.allowFireSpread")).setAllowLeavesDecay(c.getBoolean("other.allowLeavesDecay")).setAllowMobSpawning(c.getBoolean("other.allowMobSpawning"))
                    .setCanEntityIgniteBlocks(c.getBoolean("entities.canIgniteBlocks")).setAllowPistons(c.getBoolean("other.allowPistons"));
            if(c.isSet("player.allowWaterTrick")) region.setAllowWaterTrick(c.getBoolean("player.allowWaterTrick"));
            this.regions.add(region);
        }

        getRegionFile().getStringList("worldBorders").forEach(s -> {
            if(!s.contains(":")){
                return;
            }
            String[] borderInfo = s.split(":");
            World world = Bukkit.getWorld(borderInfo[0]);
            if(world == null){
                return;
            }
            if(!NumberUtil.isInt(borderInfo[1])){
                return;
            }
            int size = Integer.parseInt(borderInfo[1]);
            Location lowerCorner = new Vector(world.getSpawnLocation().getBlockX() - (size / 2), 0, world.getSpawnLocation().getBlockZ() - (size / 2)).toLocation(world);
            Location upperCorner = new Vector(world.getSpawnLocation().getBlockX() + (size / 2), 300, world.getSpawnLocation().getBlockZ() + (size / 2)).toLocation(world);
            this.worldBorders.put(world.getName(), new WorldBorder(world, upperCorner, lowerCorner, size));
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(size);
        });
    }

    public WorldBorder getWorldBorderByWorld(World world){
        return this.worldBorders.get(world.getName());
    }

    public boolean isOutOfBorder(Location location){
        return Option.of(getWorldBorderByWorld(location.getWorld())).isDefined() && !getWorldBorderByWorld(location.getWorld()).isIn(location);
    }

    public void reloadBorder(World world){
        Option.of(getWorldBorderByWorld(world)).peek(worldBorder -> {
            worldBorder.recalculateCorners();
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(worldBorder.getSize());
        });
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

    public RegionFeedback canExplode(Location location, Guild guild){
        if(location.getBlockY() >= ConfigManager.tntExplodeBelow){
            return RegionFeedback.DENY;
        }
        if(guild != null){
            if(guild.getRegion().getExplodeProtectionTime() > System.currentTimeMillis()){
                return RegionFeedback.DENY;
            }
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY;
            }
            for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                if(guildDrill.isIn(location)){
                    return RegionFeedback.DENY;
                }
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanEntityExplode() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canBuild(User user, Location location, ItemStack itemStack){
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        if(user.getUserFight().isDuringFight() && location.getBlockY() < ConfigManager.blockBuildingBelowYDuringFight){
            return RegionFeedback.DENY_BUILD_PVP_Y;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(!user.hasGuild()){
                return RegionFeedback.DENY_BUILD_GUILD;
            }
            if(!user.getGuild().equals(guild)){
                return RegionFeedback.DENY_BUILD_GUILD;
            }
            GuildMember member = user.getGuild().getMemberByName(user.getName()).orElse(null);
            if(member == null){
                return RegionFeedback.DENY_ERROR;
            }
            if(!member.hasPermission(GuildPermission.PLACE)){
                return RegionFeedback.DENY_BUILD_GUILD_PERMISSION;
            }
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY_BUILD_GUILD_CENTER;
            }
            if(guild.getRegion().getLastExplodeTime() > System.currentTimeMillis()){
                return RegionFeedback.DENY_BUILD_GUILD_CAUSE_EXPLOSION;
            }
            for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                if(guildDrill.isIn(location)){
                    return RegionFeedback.DENY_BUILD_GUILD_DRILL_AREA;
                }
            }
            if(this.plugin.getDrillManager().isSimilar(itemStack) && !guild.getOwner().equals(user)){
                return RegionFeedback.DENY_BUILD_GUILD_DRILL_OWNER;
            }
        }else{
            if(this.plugin.getDrillManager().isSimilar(itemStack)){
                return RegionFeedback.DENY_BUILD_GUILD_DRILL;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanPlayerBuild() ? RegionFeedback.ALLOW : RegionFeedback.DENY_BUILD_SPAWN;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback fakeBlockCanDestroy(User user, Location location){
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(!user.hasGuild()){
                return RegionFeedback.DENY_DESTROY_GUILD;
            }
            if(!user.getGuild().equals(guild)){
                return RegionFeedback.DENY_DESTROY_GUILD;
            }
            GuildMember member = user.getGuild().getMemberByName(user.getName()).orElse(null);
            if(member == null){
                return RegionFeedback.DENY_ERROR;
            }
            if(!member.hasPermission(GuildPermission.BREAK)){
                return RegionFeedback.DENY_DESTROY_GUILD_PERMISSION;
            }
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY_DESTROY_GUILD_CENTER;
            }
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canDestroy(User user, Location location){
        if(user == null){
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return RegionFeedback.ALLOW;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(!user.hasGuild()){
                return RegionFeedback.DENY_DESTROY_GUILD;
            }
            if(!user.getGuild().equals(guild)){
                return RegionFeedback.DENY_DESTROY_GUILD;
            }
            GuildMember member = user.getGuild().getMemberByName(user.getName()).orElse(null);
            if(member == null){
                return RegionFeedback.DENY_ERROR;
            }
            if(!member.hasPermission(GuildPermission.BREAK)){
                return RegionFeedback.DENY_DESTROY_GUILD_PERMISSION;
            }
            if(location.getBlock().getType() == Material.BEACON && !member.hasPermission(GuildPermission.DESTROY_BEACON)){
                return RegionFeedback.DENY_DESTROY_BEACON_GUILD_PERMISSION;
            }
            for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                if(guildDrill.isIn(location)){
                    return RegionFeedback.DENY_DESTROY_DRILL_GUILD;
                }
            }
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY_DESTROY_GUILD_CENTER;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            if(region.isCanPlayerDestroy()){
                if(region.isStoneGeneratorRegion()){
                    if(user.asPlayer().getItemInHand() != null && user.asPlayer().getItemInHand().getType() == Material.GOLD_PICKAXE){
                        return RegionFeedback.DENY_DESTROY_GOLD_PICKAXE;
                    }
                    if(location.getBlock().getType() != Material.STONE){
                        return RegionFeedback.DENY_DESTROY_SPAWN;
                    }
                }
                return RegionFeedback.ALLOW;
            }
            return RegionFeedback.DENY_DESTROY_SPAWN;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canDestroyFarmlands(User user, Location location){
        if(user == null) {
            return RegionFeedback.DENY_ERROR;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null) {
            if(!user.hasGuild()) {
                return RegionFeedback.DENY;
            }
            if(!user.getGuild().equals(guild)) {
                return RegionFeedback.DENY;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)) {
            return region.isCanPlayerDestroyFarmlands() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canThrowPearls(User user, Location location) {
        if(user == null) {
            return RegionFeedback.DENY_ERROR;
        }
        if(isOutOfBorder(location)){
            return RegionFeedback.DENY_PEARLS_BORDER;
        }
        if(user.canByGroup(UserGroup.ADMIN)) {
            return RegionFeedback.ALLOW;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)) {
            return region.isCanPlayerThrowPearls() ? RegionFeedback.ALLOW : RegionFeedback.DENY_PEARLS;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canMoveCauseOfBorder(Location location, Location from){
        return (isOutOfBorder(location) && !isOutOfBorder(from)) ? RegionFeedback.DENY_JOIN_BORDER : RegionFeedback.ALLOW;
    }

    public RegionFeedback canUseBuckets(User user, Location location) {
        if(user == null) {
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)) {
            return RegionFeedback.ALLOW;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY_BUCKETS_GUILD_CENTER;
            }
            for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                if(guildDrill.isIn(location)){
                    return RegionFeedback.DENY_BUCKETS_GUILD_DRILL;
                }
            }
            if(!user.hasGuild()){
                return RegionFeedback.ALLOW_WATER_TRICK;
            }
            if(!user.getGuild().equals(guild)){
                return RegionFeedback.ALLOW_WATER_TRICK;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanPlayerUserBuckets() ? RegionFeedback.ALLOW : region.isAllowWaterTrick() ? RegionFeedback.ALLOW_WATER_TRICK : RegionFeedback.DENY_BUCKETS;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canSpawnVehicles(User user, Location location) {
        if(user == null) {
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)) {
            return RegionFeedback.ALLOW;
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(!user.hasGuild()){
                return RegionFeedback.DENY_SPAWN_VEHICLES_GUILD;
            }
            if(!user.getGuild().equals(guild)){
                return RegionFeedback.DENY_SPAWN_VEHICLES_GUILD;
            }
            if(guild.getRegion().isInCenter(location)){
                return RegionFeedback.DENY_SPAWN_VEHICLES_GUILD_CENTER;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanPlayerSpawnVehicles() ? RegionFeedback.ALLOW : RegionFeedback.DENY_SPAWN_VEHICLES;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canIgniteBlocks(User user, Location location, boolean isPlayer) {
        if(isPlayer){
            if(user == null) {
                return RegionFeedback.DENY_ERROR;
            }
            if(user.canByGroup(UserGroup.ADMIN)) {
                return RegionFeedback.ALLOW;
            }
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
            if(guild != null){
                if(!user.hasGuild()){
                    return RegionFeedback.DENY;
                }
                if(!user.getGuild().equals(guild)){
                    return RegionFeedback.DENY;
                }
                if(guild.getRegion().isInCenter(location)){
                    return RegionFeedback.DENY;
                }
                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                    if(guildDrill.isIn(location)){
                        return RegionFeedback.DENY;
                    }
                }
            }
            if(checkRegions() != null){
                return checkRegions();
            }
            for(Region region : getRegionsByLocation(location)){
                return region.isCanPlayerIgniteBlocks() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
            }
            return RegionFeedback.ALLOW;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanEntityIgniteBlocks() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canIgniteTNT(User user, Location location, boolean isPlayer) {
        if(isPlayer){
            if(user == null) {
                return RegionFeedback.DENY_ERROR;
            }
            if(user.canByGroup(UserGroup.ADMIN)) {
                return RegionFeedback.ALLOW;
            }
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
            if(guild != null){
                if(!user.hasGuild()){
                    return RegionFeedback.DENY;
                }
                if(!user.getGuild().equals(guild)){
                    return RegionFeedback.DENY;
                }
                if(guild.getRegion().isInCenter(location)){
                    return RegionFeedback.DENY;
                }
                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                    if(guildDrill.isIn(location)){
                        return RegionFeedback.DENY;
                    }
                }
            }
            if(checkRegions() != null){
                return checkRegions();
            }
            for(Region region : getRegionsByLocation(location)){
                return region.isCanPlayerIgniteTNT() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
            }
            return RegionFeedback.ALLOW;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanEntityIgniteTNT() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canChangeFrames(User user, Location location, boolean isPlayer) {
        if(isPlayer){
            if(user == null) {
                return RegionFeedback.DENY_ERROR;
            }
            if(user.canByGroup(UserGroup.ADMIN)) {
                return RegionFeedback.ALLOW;
            }
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
            if(guild != null){
                if(!user.hasGuild()){
                    return RegionFeedback.DENY;
                }
                if(!user.getGuild().equals(guild)){
                    return RegionFeedback.DENY;
                }
                if(guild.getRegion().isInCenter(location)){
                    return RegionFeedback.DENY;
                }
                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                    if(guildDrill.isIn(location)){
                        return RegionFeedback.DENY;
                    }
                }
            }
            if(checkRegions() != null){
                return checkRegions();
            }
            for(Region region : getRegionsByLocation(location)){
                return region.isCanPlayerChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
            }
            return RegionFeedback.ALLOW;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanEntityChangeFrames() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canChangePaintings(User user, Location location, boolean isPlayer) {
        if(isPlayer){
            if(user == null) {
                return RegionFeedback.DENY_ERROR;
            }
            if(user.canByGroup(UserGroup.ADMIN)) {
                return RegionFeedback.ALLOW;
            }
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
            if(guild != null){
                if(!user.hasGuild()){
                    return RegionFeedback.DENY;
                }
                if(!user.getGuild().equals(guild)){
                    return RegionFeedback.DENY;
                }
                if(guild.getRegion().isInCenter(location)){
                    return RegionFeedback.DENY;
                }
                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                    if(guildDrill.isIn(location)){
                        return RegionFeedback.DENY;
                    }
                }
            }
            if(checkRegions() != null){
                return checkRegions();
            }
            for(Region region : getRegionsByLocation(location)){
                return region.isCanPlayerChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
            }
            return RegionFeedback.ALLOW;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanEntityChangePaintings() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canBeHungry(Location location) {
        for(Region region : getRegionsByLocation(location)){
            return region.isCanPlayerBeHungry() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canJoinDuringPVP(User user, Location location, Location from) {
        if(user == null) {
            return RegionFeedback.DENY_ERROR;
        }
        if(user.canByGroup(UserGroup.ADMIN)) {
            return RegionFeedback.ALLOW;
        }
        if(user.getUserFight().isDuringFight()){
            if(checkRegions() != null){
                return checkRegions();
            }
            Region regionFrom = getRegionByLocation(from);
            if(regionFrom != null && !regionFrom.isCanPlayerJoinDuringPVP()){
                return RegionFeedback.ALLOW;
            }
            for(Region region : getRegionsByLocation(location)){
                return region.isCanPlayerJoinDuringPVP() ? RegionFeedback.ALLOW : RegionFeedback.DENY_JOIN_SPAWN_CAUSE_PVP;
            }
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback allowFireSpread(Location location) {
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            return RegionFeedback.DENY;
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isAllowFireSpread() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback allowMobSpawning(Location location) {
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isAllowMobSpawning() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback allowLeavesDecay(Location location) {
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isAllowLeavesDecay() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canHurt(Location location){
        if(checkRegions() != null){
            return checkRegions();
        }
        for(Region region : getRegionsByLocation(location)){
            return region.isCanPlayerBeHurt() ? RegionFeedback.ALLOW : RegionFeedback.DENY;
        }
        return RegionFeedback.ALLOW;
    }

    public RegionFeedback canHurt(User damager, User victim){
        if(damager == null || victim == null) {
            return RegionFeedback.DENY_ERROR;
        }
        if(damager.hasGuild() && victim.hasGuild()){
            if(damager.getGuild().equals(victim.getGuild())){
                if(!damager.getGuild().isFriendlyFire()){
                    return RegionFeedback.DENY_PVP_OWN_GUILD;
                }
            }
            if(this.plugin.getAllianceManager().hasAlliance(damager.getGuild(), victim.getGuild())){
                return RegionFeedback.DENY_PVP_ALLIANCE;
            }
        }
        if(checkRegions() != null){
            return checkRegions();
        }
        Region damagerRegion = getRegionByLocation(damager.asPlayer().getLocation().getBlock().getLocation());
        Region victimRegion = getRegionByLocation(victim.asPlayer().getLocation().getBlock().getLocation());
        if(damagerRegion == null && victimRegion == null){
            return RegionFeedback.ALLOW;
        }
        if(damagerRegion == null){
            if(victimRegion.isCanPlayerHurtOther()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY;
            }
        }
        if(victimRegion == null){
            if(damagerRegion.isCanPlayerHurtOther()){
                return RegionFeedback.ALLOW;
            }else{
                return RegionFeedback.DENY_PVP_OTHER_REGION;
            }
        }
        if(!victimRegion.isCanPlayerHurtOther() && !damagerRegion.isCanPlayerHurtOther()){
            return RegionFeedback.DENY;
        }
        if(damagerRegion.isCanPlayerHurtOther() && !victimRegion.isCanPlayerHurtOther()){
            return RegionFeedback.DENY;
        }
        if(!damagerRegion.isCanPlayerHurtOther() && victimRegion.isCanPlayerHurtOther()){
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
        List<String> allowedDuringCheck = Arrays.asList("/msg", "/r", "/helpop");
        if(PlayerCheckUtil.getPlayerSet().contains(user.asPlayer()) && !allowedDuringCheck.contains(command.toLowerCase())){
            return RegionFeedback.DENY_PROCCESS_COMMAND_CAUSE_CHECK;
        }
        if(user.getUserFight().isDuringFight()){
            if(!this.plugin.getSimpleCommandManager().getAllowedDuringPVP().contains(command.toLowerCase())){
                return RegionFeedback.DENY_PROCCESS_COMMAND_FIGHT;
            }
        }
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(guild != null){
            if(!user.hasGuild() || (user.hasGuild() && !user.getGuild().equals(guild))){
                if(this.plugin.getSimpleCommandManager().getBlockedCommandsInGuild().contains(command.toLowerCase())) {
                    return RegionFeedback.DENY_PROCCESS_COMMAND_GUILD;
                }
            }
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
