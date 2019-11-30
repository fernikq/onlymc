package pl.fernikq.core.guild;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildMemberData;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.guild.region.GuildRegion;
import pl.fernikq.core.guild.region.GuildRegionData;
import pl.fernikq.core.guild.treasure.GuildTreasureData;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.SpaceUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GuildManager {

    private final CorePlugin plugin;
    private ConcurrentMap<String, Guild> guilds;
    private GuildData guildData;
    private GuildRegionData guildRegionData;
    private GuildTreasureData guildTreasureData;
    private GuildMemberData guildMemberData;

    public GuildManager(CorePlugin plugin){
        this.plugin = plugin;
        this.guilds = new ConcurrentHashMap<>();
    }

    public void init(){
        this.guildData = new GuildData(this.plugin);
        this.guildMemberData = new GuildMemberData(this.plugin);
        this.guildRegionData = new GuildRegionData(this.plugin);
        this.guildTreasureData = new GuildTreasureData(this.plugin);
    }

    public Option<Guild> getGuildByTag(String tag){
        return Option.of(this.guilds.get(tag.toUpperCase()));
    }

    public Option<Guild> getGuildByName(String name){
        return getGuilds().find(guild -> guild.getName().equalsIgnoreCase(name));
    }

    public Option<Guild> getGuildByLocation(Location location){
        return getGuilds().find(guild -> guild.getRegion().isIn(location));
    }

    public void updateGuild(Guild guild){
        this.guildData.updateGuild(guild);
        this.guildRegionData.updateRegion(guild.getRegion());
        this.guildTreasureData.updateTreasure(guild.getTreasure());
        guild.getMembers().forEach(member -> this.guildMemberData.updateMember(member));
    }

    public void registerGuild(Guild guild){
        this.guilds.putIfAbsent(guild.getTag().toUpperCase(), guild);
    }

    public void addMember(User user, Guild guild, GuildPermission... guildPermissions){
        GuildMember member = new GuildMember(user, guild, guildPermissions);
        this.guildMemberData.insertMember(member);
        this.plugin.getTagManager().updateTag(member.getUser().asPlayer());
    }

    public void removeMember(GuildMember member){
        member.getGuild().removeMember(member);
        this.guildMemberData.deleteMember(member);
        this.plugin.getTagManager().updateTag(member.getUser().asPlayer());
    }

    public void createGuild(Player owner, String tag, String name){
        Location center = owner.getLocation().clone();
        center.setY(ConfigManager.guildCenterY);
        this.plugin.getUserManager().getUser(owner.getUniqueId()).peek(user -> {
            Guild guild = new Guild(user, tag.toUpperCase(), name);
            GuildRegion region = new GuildRegion(guild, center.getBlock().getLocation());
            addMember(user, guild, GuildPermission.values());
            createGuildRoom(guild);
            registerGuild(guild);
            this.guildData.insertGuild(guild);
            this.guildRegionData.insertRegion(region);
            this.guildTreasureData.insertTreasure(guild.getTreasure());
            owner.teleport(region.getHome());
        });
    }

    public void deleteGuild(Guild guild){
        this.guildData.deleteGuild(guild);
        this.guildTreasureData.deleteTreasure(guild.getTreasure());
        this.guildRegionData.deleteRegion(guild.getRegion());
        guild.getMembers().forEach(member -> removeMember(member));
        this.plugin.getAllianceManager().getAllies(guild).forEach(ally -> this.plugin.getAllianceManager().removeAlliance(ally, guild));
        this.guilds.remove(guild.getTag().toUpperCase());
        deleteGuildRoom(guild);
    }

    private void deleteGuildRoom(Guild guild){
        Location center = guild.getRegion().getCenter().clone();
        center.setY(ConfigManager.guildCenterY);
        center.getBlock().setType(Material.AIR);
        center.setY(ConfigManager.guildCenterY - 1);
        center.getBlock().setType(Material.AIR);
        guild.getRegion().getCenter().getWorld().save();
    }

    private void createGuildRoom(Guild guild){
        Location center = guild.getRegion().getCenter().clone();
        center.setY(ConfigManager.guildCenterY - 1);
        for (final Location loc : SpaceUtil.getSquare(center, 4, 5)) {
            loc.getBlock().setType(Material.AIR);
        }
        for(final Location loc : SpaceUtil.getSquare(center, 3, 0)) {
            loc.getBlock().setType(Material.STEP);
        }
        for (final Location loc : SpaceUtil.getSquare(center, 2, 0)) {
            loc.getBlock().setType(Material.OBSIDIAN);
        }
        for (final Location loc : SpaceUtil.getCorners(center, 3, 5)) {
            loc.getBlock().setType(Material.OBSIDIAN);
        }
        center.getBlock().setType(Material.BEDROCK);
        center.setY(ConfigManager.guildCenterY);
        center.getBlock().setType(Material.ENDER_PORTAL_FRAME);
        center.setY(ConfigManager.guildCenterY + 4);
        for (final Location loc : SpaceUtil.getSquare(center, 3, 0)) {
            loc.getBlock().setType(Material.OBSIDIAN);
        }
        guild.getRegion().getCenter().getWorld().save();
    }

    public boolean isInCenter(Location location){
        return getGuilds().find(guild -> guild.getRegion().isInCenter(location)).isDefined();
    }

    public boolean isNearGuild(Location location){
        int minimalDistance = ConfigManager.minimalDistanceBetweenGuilds;
        minimalDistance += (ConfigManager.guildStartCuboidSize + 2);
        for(Integer integer : ConfigManager.guildCuboidSizeEnlargeCost){
            minimalDistance += ConfigManager.guildCuboidSizeAddByEnlarge;
        }
        for(Guild guild : getGuilds()){
            if((Math.abs(guild.getRegion().getCenter().getBlockX() - location.getBlockX()) <= minimalDistance) && (Math.abs(guild.getRegion().getCenter().getBlockZ() - location.getBlockZ()) <= minimalDistance)){
                return true;
            }
        }
        return false;
    }

    public boolean isNearSpawn(Location location){
        int minimalDistance = ConfigManager.minimalDistanceFromSpawn;
        Location spawn = LocationUtil.locationFromString(ConfigManager.spawnLocation);
        if((Math.abs(spawn.getBlockX() - location.getBlockX()) <= minimalDistance) && (Math.abs(spawn.getBlockZ() - location.getBlockZ()) <= minimalDistance)) {
            return true;
        }
        return false;
    }

    public boolean hasItems(Player player){
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null && user.canByGroup(UserGroup.VIP)){
            for(String item : ConfigManager.guildVipItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                int have = ItemUtil.getAmountOfMaterial(player.getInventory(), material, data);
                if(have < amount) {
                    return false;
                }
            }
            return true;
        }
        for(String item : ConfigManager.guildPlayerItemsToCreate) {
            String[] itemInfo = item.split(":");
            Material material = ItemUtil.getMaterial(itemInfo[0]);
            int amount = Integer.parseInt(itemInfo[2]);
            short data = Short.parseShort(itemInfo[1]);
            int have = ItemUtil.getAmountOfMaterial(player.getInventory(), material, data);
            if(have < amount) {
                return false;
            }
        }
        return true;
    }

    public void removeItems(Player player){
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null && user.canByGroup(UserGroup.VIP)){
            for(String item : ConfigManager.guildVipItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                ItemUtil.remove(new ItemStack(material, 1, data), player, amount);
            }
            return;
        }
        for(String item : ConfigManager.guildPlayerItemsToCreate) {
            String[] itemInfo = item.split(":");
            Material material = ItemUtil.getMaterial(itemInfo[0]);
            int amount = Integer.parseInt(itemInfo[2]);
            short data = Short.parseShort(itemInfo[1]);
            ItemUtil.remove(new ItemStack(material, 1, data), player, amount);
        }
    }

    public Set<Guild> getGuilds(){
        return HashSet.ofAll(this.guilds.values());
    }
}
