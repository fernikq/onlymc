package pl.fernikq.core.user.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.fernikq.core.user.User;

import java.util.UUID;

public class Home {

    private String name;
    private User owner;
    private Location location;

    public Home(User user, String name, Location location){
        this.owner = user;
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }
}
