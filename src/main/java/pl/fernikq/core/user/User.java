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
    private Map<String, InventoryGUI> inventories;


    public User(Player player){
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.firstAddress = player.getAddress().getAddress().getHostAddress();
        this.lastAddress = player.getAddress().getAddress().getHostAddress();
        this.group = UserGroup.PLAYER;
        this.inventories = new HashMap<>();
        this.homes = new HashMap<>();
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

    public Player asPlayer(){
        return Bukkit.getPlayerExact(this.name);
    }

    public boolean isOnline(){
        return asPlayer() != null;
    }

    public Map<String, InventoryGUI> getInventories() {
        return inventories;
    }

    public Map<String, Home> getHomes() {
        return homes;
    }
}
