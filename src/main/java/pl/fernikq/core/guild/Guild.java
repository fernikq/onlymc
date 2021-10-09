package pl.fernikq.core.guild;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.drill.GuildDrill;
import pl.fernikq.core.guild.logblock.LogBlock;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.region.GuildRegion;
import pl.fernikq.core.guild.treasure.GuildTreasure;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Guild {

    private User owner;
    private String tag;
    private String name;
    private List<GuildMember> members;
    private Cache<Guild, Long> alliesRequest;
    private Cache<User, Long> membersRequest;
    private GuildRegion region;
    private GuildTreasure treasure;
    private long creationTime;
    private long lastAttackTime;
    private int maxMembers;
    private int maxAllies;
    private boolean friendlyFire;
    private long expireTime;
    private int health;
    private Cache<Guild, Long> preDeleted;
    private Map<Location, GuildDrill> guildDrills;
    private Map<Location, List<LogBlock>> logblockMap;

    private int enlargeMembersLevel;
    private int enlargeAlliesLevel;

    public Guild(User owner, String tag, String name){
        this.owner = owner;
        this.tag = tag;
        this.name = name;
        this.members = new ArrayList<>();
        this.alliesRequest = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        this.membersRequest = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        this.preDeleted = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
        this.treasure = new GuildTreasure(this);
        this.creationTime = System.currentTimeMillis();
        this.lastAttackTime = System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.guildAttackProtectionAfterCreate);
        this.maxMembers = ConfigManager.guildMaxStartMembersSize;
        this.maxAllies = ConfigManager.guildMaxStartAlliesSize;
        this.friendlyFire = false;
        this.expireTime = System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.guildExpireAfterCreateTime);
        this.health = ConfigManager.guildStartHealth;
        this.enlargeAlliesLevel = 0;
        this.enlargeMembersLevel = 0;
        this.guildDrills = new HashMap<>();
        this.logblockMap = new HashMap<>();
    }

    public Guild(User owner, ResultSet resultSet){
        try {
            this.owner = owner;
            this.tag = resultSet.getString("tag");
            this.name = resultSet.getString("name");
            this.members = new ArrayList<>();
            this.alliesRequest = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
            this.membersRequest = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
            this.preDeleted = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
            this.creationTime = resultSet.getLong("creationTime");
            this.lastAttackTime = resultSet.getLong("lastAttackTime");
            this.maxMembers = resultSet.getInt("maxMembers");
            this.maxAllies = resultSet.getInt("maxAllies");
            this.expireTime = resultSet.getLong("expireTime");
            this.health = resultSet.getInt("health");
            this.enlargeAlliesLevel = resultSet.getInt("enlargeAlliesLevel");
            this.enlargeMembersLevel = resultSet.getInt("enlargeMembersLevel");
            this.friendlyFire = false;
            this.guildDrills = new HashMap<>();
            this.logblockMap = new HashMap<>();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GuildMember> getMembers() {
        return new ArrayList<>(this.members);
    }

    public Map<Location, GuildDrill> getGuildDrills() {
        return new HashMap<>(this.guildDrills);
    }

    public void addDrill(GuildDrill guildDrill){
        this.guildDrills.put(guildDrill.getCenter(), guildDrill);
    }

    public void removeDrill(GuildDrill guildDrill){
        this.guildDrills.remove(guildDrill.getCenter());
    }

    public GuildDrill getDrillByLocation(Location location){
        return this.guildDrills.get(location);
    }

    public void addMember(GuildMember member){
        if(!this.members.contains(member)) this.members.add(member);
        member.setGuild(this);
    }

    public void removeMember(GuildMember member){
        if(this.members.contains(member)) this.members.remove(member);
        member.getUser().setGuild(null);
    }

    public Optional<GuildMember> getMemberByName(String name){
        return getMembers().stream().filter(member -> member.getUser().getName().equalsIgnoreCase(name)).findFirst();
    }

    public void setMembers(List<GuildMember> members) {
        this.members = members;
    }

    public Cache<Guild, Long> getAlliesRequest() {
        return alliesRequest;
    }

    public Cache<User, Long> getMembersRequest() {
        return membersRequest;
    }

    public GuildRegion getRegion() {
        return region;
    }

    public void setRegion(GuildRegion region) {
        this.region = region;
    }

    public GuildTreasure getTreasure() {
        return treasure;
    }

    public void setTreasure(GuildTreasure treasure) {
        this.treasure = treasure;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long createTime) {
        this.creationTime = createTime;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Cache<Guild, Long> getPreDeleted() {
        return preDeleted;
    }

    public int getKills(){
        int kills = this.members.stream().mapToInt(member -> member.getUser().getUserStat().getKills()).sum();
        return kills;
    }

    public int getLogouts(){
        return this.members.stream().mapToInt(member -> member.getUser().getUserStat().getLogouts()).sum();
    }

    public int getDeaths(){
        int deaths = this.members.stream().mapToInt(member -> member.getUser().getUserStat().getDeaths()).sum();
        return deaths;
    }

    public int getPoints(){
        if(this.members.isEmpty()){
            return -1;
        }
        int points = this.members.stream().mapToInt(member -> member.getUser().getUserStat().getPoints()).sum();
        return points / this.members.size();
    }

    public int getAssists(){
        int assists = this.members.stream().mapToInt(member -> member.getUser().getUserStat().getAssists()).sum();
        return assists;
    }

    public int getEnlargeMembersLevel() {
        return enlargeMembersLevel;
    }

    public void setEnlargeMembersLevel(int enlargeMembersLevel) {
        this.enlargeMembersLevel = enlargeMembersLevel;
    }

    public int getEnlargeAlliesLevel() {
        return enlargeAlliesLevel;
    }

    public void setEnlargeAlliesLevel(int enlargeAlliesLevel) {
        this.enlargeAlliesLevel = enlargeAlliesLevel;
    }

    public List<GuildMember> getOnlineMembers(){
        return this.members.stream().filter(member -> member.getUser().asPlayer() != null).collect(Collectors.toList());
    }

    public List<LogBlock> getLogBlocksAtLocation(Location location){
        List<LogBlock> logBlocks = this.logblockMap.get(location);
        return Objects.isNull(logBlocks) ? new ArrayList<LogBlock>() : new ArrayList<>(logBlocks);
    }

    public void addLogBlockAtLocation(LogBlock logBlock){
        Location location = logBlock.getLocation();
        List<LogBlock> logBlocks = this.logblockMap.get(location);
        if(Objects.isNull(logBlocks)){
            logBlocks = new ArrayList<LogBlock>();
            logBlocks.add(0, logBlock);
            this.logblockMap.put(location, logBlocks);
            return;
        }
        logBlocks.add(0, logBlock);
    }
}
