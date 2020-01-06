package pl.fernikq.core.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.fernikq.core.config.ConfigManager;

public class WorldBorder {

    private World world;
    private Location upperCorner;
    private Location lowerCorner;
    private int size;

    public WorldBorder(World world, Location upperCorner, Location lowerCorner, int size) {
        this.world = world;
        this.upperCorner = upperCorner;
        this.lowerCorner = lowerCorner;
        this.size = size;
    }

    public void recalculateCorners(){
        this.upperCorner = new Vector(this.world.getSpawnLocation().getBlockX() + (size / 2), 256, this.world.getSpawnLocation().getBlockZ() + (size / 2)).toLocation(this.world);
        this.lowerCorner = new Vector(this.world.getSpawnLocation().getBlockX() - (size / 2), 0, this.world.getSpawnLocation().getBlockZ() - (size / 2)).toLocation(this.world);
    }

    public boolean isIn(Location location){
        if(!location.getWorld().getName().equals(this.world.getName())){
            return false;
        }
        return location.toVector().isInAABB(this.lowerCorner.toVector(), this.upperCorner.toVector());
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
