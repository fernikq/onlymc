package pl.fernikq.core.user.home;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HomeData {

    private final CorePlugin plugin;

    public HomeData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadHomes();
    }

    public void checkTable(){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_homes` ("+
                    "`owner` VARCHAR(128) NOT NULL,"+
                    "`name` TEXT NOT NULL,"+
                    "`location` TEXT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadHomes(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet rs = this.plugin.getMySQL().query("SELECT * FROM `core_homes`");
            while(rs.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(rs.getString("owner"))).peek(user -> {
                    try {
                        Home home = new Home(user, rs.getString("name"), LocationUtil.locationFromString(rs.getString("location")));
                        user.getHomes().put(home.getName().toLowerCase(), home);
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertHome(Home home){
        try {
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_homes` (owner, name, location) VALUES ("+
                    "?, ?, ?);");
            statement.setString(1, home.getOwner().getUuid().toString());
            statement.setString(2, home.getName());
            statement.setString(3, LocationUtil.locationToString(home.getLocation()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(Home home){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_homes` WHERE `owner` = '"+home.getOwner().getUuid().toString()+"' AND `name` = '"+home.getName()+"';");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Home home){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("UPDATE `core_homes` SET `location` = '"+LocationUtil.locationToString(home.getLocation())+"' "+
                    "WHERE `owner` = '"+home.getOwner().getUuid().toString()+"' AND `name` = '"+home.getName()+"';");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
