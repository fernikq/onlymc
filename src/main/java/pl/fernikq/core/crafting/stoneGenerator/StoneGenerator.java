package pl.fernikq.core.crafting.stoneGenerator;

import org.bukkit.Location;

import java.io.File;

public class StoneGenerator {

    private Location location;
    private long regenerationTime;

    public StoneGenerator(Location location) {
        this.location = location;
        this.regenerationTime = 0;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getRegenerationTime() {
        return regenerationTime;
    }

    public void setRegenerationTime(long regenerationTime) {
        this.regenerationTime = regenerationTime;
    }
}
