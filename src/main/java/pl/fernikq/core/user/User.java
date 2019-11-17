package pl.fernikq.core.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import pl.fernikq.core.dummy.Dummy;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.home.Home;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class User {

    private UUID uuid;
    private String name;
    private String firstAddress;
    private String lastAddress;
    private UserGroup group;
    private UserStat userStat;
    private UserChat userChat;

    private Map<String, Home> homes;
    private boolean godMode;
    private Map<String, InventoryGUI> inventories;
    private Map<String, Long> kitTimes;
    private Cache<User, Long> tpaRequests;
    private User privateMessageSender;
    private Scoreboard scoreboard;
    private Dummy dummy;

    public User(Player player){
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.firstAddress = player.getAddress().getAddress().getHostAddress();
        this.lastAddress = player.getAddress().getAddress().getHostAddress();
        this.group = UserGroup.PLAYER;
        this.inventories = new HashMap<>();
        this.homes = new HashMap<>();
        this.godMode = false;
        this.privateMessageSender = null;
        this.kitTimes = new HashMap<>();
        this.userStat = new UserStat(this);
        this.userChat = new UserChat(this);
        this.dummy = new Dummy(this);
        this.tpaRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    public User(ResultSet rs){
        try {
            this.uuid = UUID.fromString(rs.getString("uuid"));
            this.name = rs.getString("name");
            this.firstAddress = rs.getString("firstAddress");
            this.lastAddress = rs.getString("lastAddress");
            this.group = UserGroup.getByName(rs.getString("groupName"));
            this.inventories = new HashMap<>();
            this.homes = new HashMap<>();
            this.godMode = false;
            this.privateMessageSender = null;
            this.kitTimes = new HashMap<>();
            this.userChat = new UserChat(this);
            this.dummy = new Dummy(this);
            this.tpaRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canByGroup(UserGroup group){
        return this.group.getLevel() >= group.getLevel();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstAddress() {
        return firstAddress;
    }

    public Map<String, Long> getKitTimes() {
        return new HashMap<>(this.kitTimes);
    }

    public void setKitTimes(Map<String, Long> kitTimes) {
        this.kitTimes = kitTimes;
    }

    public void setKitTime(String kitName, long time){
        this.kitTimes.put(kitName, time);
    }

    public void setFirstAddress(String firstAddress) {
        this.firstAddress = firstAddress;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public User getPrivateMessageSender() {
        return privateMessageSender;
    }

    public void setPrivateMessageSender(User privateMessageSender) {
        this.privateMessageSender = privateMessageSender;
    }

    public Player asPlayer(){
        return Bukkit.getPlayerExact(this.name);
    }

    public boolean isOnline(){
        return asPlayer() != null;
    }

    public Map<String, InventoryGUI> getInventories() {
        return new HashMap<>(this.inventories);
    }

    public void addInventory(InventoryGUI inventoryGUI){
        this.inventories.put(inventoryGUI.getInventory().getName(), inventoryGUI);
    }

    public Map<String, Home> getHomes() {
        return new HashMap<>(this.homes);
    }

    public void addHome(Home home){
        this.homes.putIfAbsent(home.getName().toLowerCase(), home);
    }

    public void removeHome(Home home){
        this.homes.remove(home.getName().toLowerCase());
    }

    public List<Home> getHomeList(){
        return new ArrayList<Home>(this.homes.values());
    }

    public Cache<User, Long> getTpaRequests() {
        return tpaRequests;
    }

    public List<User> getTpaRequestsList(){
        return new ArrayList<>(this.tpaRequests.asMap().keySet());
    }

    public UserStat getUserStat() {
        return userStat;
    }

    public void setUserStat(UserStat userStat) {
        this.userStat = userStat;
    }

    public UserChat getUserChat() {
        return userChat;
    }

    public void setUserChat(UserChat userChat) {
        this.userChat = userChat;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Dummy getDummy() {
        return dummy;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }
}
