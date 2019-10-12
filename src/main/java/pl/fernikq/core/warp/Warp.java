package pl.fernikq.core.warp;

import org.bukkit.Location;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.LocationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Warp {

    private String name;
    private Location location;
    private UserGroup requiredGroup;

    public Warp(String name, Location location, UserGroup group){
        this.name = name;
        this.location = location;
        this.requiredGroup = group;
    }

    public Warp(ResultSet resultSet){
        try {
            this.name = resultSet.getString("name");
            this.location = LocationUtil.locationFromString(resultSet.getString("location"));
            this.requiredGroup = UserGroup.getByName(resultSet.getString("requiredGroup"));
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UserGroup getRequiredGroup() {
        return requiredGroup;
    }

    public void setRequiredGroup(UserGroup requiredGroup) {
        this.requiredGroup = requiredGroup;
    }
}
