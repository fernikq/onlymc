package pl.fernikq.core.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.home.Home;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {

    private UUID uuid;
    private String name;
    private String firstAddress;
    private String lastAddress;
    private UserGroup group;

    private Map<String, Home> homes;
    private boolean godMode;
    private Map<String, InventoryGUI> inventories;
    private Map<String, Long> kitTimes;
    private User privateMessageSender;


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

    public void addKitTime(String kitName, long time){
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
        this.inventories.putIfAbsent(inventoryGUI.getInventory().getName(), inventoryGUI);
    }

    public Map<String, Home> getHomes() {
        return new HashMap<>(this.homes);
    }

    public List<Home> getHomeList(){
        return new ArrayList<Home>(this.homes.values());
    }
}
