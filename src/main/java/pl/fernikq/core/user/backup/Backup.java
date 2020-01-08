package pl.fernikq.core.user.backup;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Backup {

    private User user;
    private ItemStack[] items;
    private ItemStack[] armor;
    private int points;
    private int deaths;
    private long deathTime;
    private int ping;
    private String reason;

    private boolean giveItems;
    private boolean giveArmor;
    private boolean givePoints;
    private boolean giveDeaths;

    public Backup(){
        this.giveArmor = true;
        this.giveDeaths = true;
        this.giveItems = true;
        this.givePoints = true;
    }

    public Backup(User user, ResultSet resultSet){
        try {
            this.user = user;
            this.items = SerializationUtil.itemStackFromString(resultSet.getString("items"));
            this.armor = SerializationUtil.itemStackFromString(resultSet.getString("armor"));
            this.points = resultSet.getInt("points");
            this.points = resultSet.getInt("deaths");
            this.deathTime = resultSet.getLong("creationTime");
            this.ping = resultSet.getInt("ping");
            this.reason = resultSet.getString("reason");
            this.giveArmor = true;
            this.giveDeaths = true;
            this.giveItems = true;
            this.givePoints = true;
            user.getBackups().add(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public long getDeathTime() {
        return deathTime;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

    public boolean isGiveItems() {
        return giveItems;
    }

    public void setGiveItems(boolean giveItems) {
        this.giveItems = giveItems;
    }

    public boolean isGiveArmor() {
        return giveArmor;
    }

    public void setGiveArmor(boolean giveArmor) {
        this.giveArmor = giveArmor;
    }

    public boolean isGivePoints() {
        return givePoints;
    }

    public void setGivePoints(boolean givePoints) {
        this.givePoints = givePoints;
    }

    public boolean isGiveDeaths() {
        return giveDeaths;
    }

    public void setGiveDeaths(boolean giveDeaths) {
        this.giveDeaths = giveDeaths;
    }
}
