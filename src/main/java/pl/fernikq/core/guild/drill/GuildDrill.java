package pl.fernikq.core.guild.drill;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GuildDrill {

    private Guild guild;
    private Location center;
    private Location upperCorner;
    private Location lowerCorner;
    private Material material;
    private Inventory inventory;
    private int level;

    public GuildDrill(Guild guild, Location center, Material material) {
        this.guild = guild;
        this.center = center.clone();
        this.lowerCorner = new Vector(this.center.getBlockX() - 2, this.center.getBlockY() - 2, this.center.getBlockZ() - 2).toLocation(this.center.getWorld());
        this.upperCorner = new Vector(this.center.getBlockX() + 2, this.center.getBlockY() + 2, this.center.getBlockZ() + 2).toLocation(this.center.getWorld());
        this.material = material;
        this.inventory = Bukkit.createInventory(null, 54, ChatUtil.fixColor("&8[ {c}&lMagazyn wiertla &8]"));
        this.level = 0;
    }

    public GuildDrill(Guild guild, ResultSet resultSet){
        try {
            this.guild = guild;
            this.center = LocationUtil.locationFromString(resultSet.getString("location"));
            this.lowerCorner = new Vector(this.center.getBlockX() - 2, this.center.getBlockY() - 2, this.center.getBlockZ() - 2).toLocation(this.center.getWorld());
            this.upperCorner = new Vector(this.center.getBlockX() + 2, this.center.getBlockY() + 2, this.center.getBlockZ() + 2).toLocation(this.center.getWorld());
            this.inventory = Bukkit.createInventory(null, 54, ChatUtil.fixColor("&8[ {c}&lMagazyn wiertla &8]"));
            this.inventory.setContents(SerializationUtil.itemStackFromString(resultSet.getString("inventory")));
            this.material = Material.getMaterial(resultSet.getString("material"));
            this.level = resultSet.getInt("level");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIn(Location location){
        if(!location.getWorld().equals(this.center.getWorld())){
            return false;
        }
        return location.toVector().isInAABB(this.lowerCorner.toVector(), this.upperCorner.toVector());
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSpeedByLevel(){
        if(this.level < 1) return 6000;
        if(this.level == 1) return 4800;
        if(this.level == 2) return 3600;
        return 2400;
    }

    public int[] getAmountByLevel(){
        if(this.level < 1) return new int[]{1, 3};
        if(this.level == 1) return new int[]{2, 5};
        if(this.level == 2) return new int[]{3, 7};
        return new int[]{4, 9};
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Location getUpperCorner() {
        return upperCorner;
    }

    public void setUpperCorner(Location upperCorner) {
        this.upperCorner = upperCorner;
    }

    public Location getLowerCorner() {
        return lowerCorner;
    }

    public void setLowerCorner(Location lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
