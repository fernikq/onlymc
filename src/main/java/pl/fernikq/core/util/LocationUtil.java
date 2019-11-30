package pl.fernikq.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location locationFromString(String s) {
        String[] ss = s.split(",");
        Location l = new Location(Bukkit.getWorlds().get(0),0.0,0.0,0.0,0.0f,0.0f);
        l.setWorld(Bukkit.getWorld(ss[0]));
        l.setX(Double.parseDouble(ss[1]));
        l.setY(Double.parseDouble(ss[2]));
        l.setZ(Double.parseDouble(ss[3]));
        l.setYaw(Float.parseFloat(ss[4]));
        l.setPitch(Float.parseFloat(ss[5]));
        return l;
    }
    public static String locationToString(Location l) {
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    public static boolean move(Location l, Location x) {
        return l.getBlockX() != x.getBlockX() || l.getBlockY() != x.getBlockY() || l.getBlockZ() != x.getBlockZ();
    }

    public static boolean moveXZ(Location l, Location x){
        return l.getBlockX() != x.getBlockX() || l.getBlockZ() != x.getBlockZ();
    }
}
