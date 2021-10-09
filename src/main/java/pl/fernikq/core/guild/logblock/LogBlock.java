package pl.fernikq.core.guild.logblock;

import org.bukkit.Location;

public class LogBlock {

    private Location location;
    private long time;
    private String userName;
    private LogBlockActionType logBlockActionType;
    private String blockType;

    public LogBlock(Location location, long time, String userName, LogBlockActionType logBlockActionType, String blockType) {
        this.location = location;
        this.time = time;
        this.userName = userName;
        this.logBlockActionType = logBlockActionType;
        this.blockType = blockType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LogBlockActionType getLogBlockActionType() {
        return logBlockActionType;
    }

    public void setLogBlockActionType(LogBlockActionType logBlockActionType) {
        this.logBlockActionType = logBlockActionType;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }
}
