package pl.fernikq.core.guild.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildRegion {

    private Guild guild;
    private Location lowerCorner;
    private Location upperCorner;
    private int size;
    private long lastExplodeTime;
    private long explodeProtectionTime;
    private Location home;
    private Location center;
    private Location centerLowerCorner;
    private Location centerUpperCorner;

    private int enlargeRegionLevel;

    public GuildRegion(Guild guild, Location location){
        this.guild = guild;
        this.size = ConfigManager.guildStartCuboidSize;
        this.explodeProtectionTime = System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.guildExplosionProtectionAfterCreate);
        this.lastExplodeTime = System.currentTimeMillis();
        this.home = location.clone().add(0, 2, 0);
        this.center = location.clone();
        this.centerLowerCorner = new Vector(this.center.getBlockX() - 2, this.center.getBlockY() - 1, this.center.getBlockZ() - 2).toLocation(this.center.getWorld());
        this.centerUpperCorner = new Vector(this.center.getBlockX() + 2, this.center.getBlockY() + 4, this.center.getBlockZ() + 2).toLocation(this.center.getWorld());
        this.enlargeRegionLevel = 0;
        setRegionCorners();
        this.guild.setRegion(this);
    }

    public GuildRegion(Guild guild, ResultSet resultSet){
        try {
            this.guild = guild;
            this.size = resultSet.getInt("size");
            this.explodeProtectionTime = resultSet.getLong("explodeProtectionTime");
            this.home = LocationUtil.locationFromString(resultSet.getString("home"));
            this.center = LocationUtil.locationFromString(resultSet.getString("center"));
            this.enlargeRegionLevel = resultSet.getInt("enlargeRegionLevel");
            this.centerLowerCorner = new Vector(this.center.getBlockX() - 2, this.center.getBlockY() - 1, this.center.getBlockZ() - 2).toLocation(this.center.getWorld());
            this.centerUpperCorner = new Vector(this.center.getBlockX() + 2, this.center.getBlockY() + 4, this.center.getBlockZ() + 2).toLocation(this.center.getWorld());
            this.guild.setRegion(this);
            setRegionCorners();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRegionCorners(){
        this.lowerCorner = new Vector(this.center.getBlockX() - size, 0, this.center.getBlockZ() - size).toLocation(this.center.getWorld());
        this.upperCorner = new Vector(this.center.getBlockX() + size, 256, this.center.getBlockZ() + size).toLocation(this.center.getWorld());
    }

    public boolean isIn(Location location){
        if(!location.getWorld().equals(this.center.getWorld())){
            return false;
        }
        return location.toVector().isInAABB(this.lowerCorner.toVector(), this.upperCorner.toVector());
    }

    public boolean isInCenter(Location location){
        if(!location.getWorld().equals(this.center.getWorld())){
            return false;
        }
        if(!isIn(location)){
            return false;
        }
        return location.toVector().isInAABB(this.centerLowerCorner.toVector(), this.centerUpperCorner.toVector());
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Location getLowerCorner() {
        return lowerCorner;
    }

    public void setLowerCorner(Location lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    public Location getUpperCorner() {
        return upperCorner;
    }

    public void setUpperCorner(Location upperCorner) {
        this.upperCorner = upperCorner;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getLastExplodeTime() {
        return lastExplodeTime;
    }

    public void setLastExplodeTime(long lastExplodeTime) {
        this.lastExplodeTime = lastExplodeTime;
    }

    public long getExplodeProtectionTime() {
        return explodeProtectionTime;
    }

    public void setExplodeProtectionTime(long explodeProtectionTime) {
        this.explodeProtectionTime = explodeProtectionTime;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getEnlargeRegionLevel() {
        return enlargeRegionLevel;
    }

    public void setEnlargeRegionLevel(int enlargeRegionLevel) {
        this.enlargeRegionLevel = enlargeRegionLevel;
    }
}
