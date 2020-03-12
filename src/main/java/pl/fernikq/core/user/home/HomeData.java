package pl.fernikq.core.user.home;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.LocationUtil;

import java.sql.Connection;
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
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_homes` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`owner` VARCHAR(128) NOT NULL,"+
                    "`name` TEXT NOT NULL,"+
                    "`location` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadHomes(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet rs = connection.prepareStatement("SELECT * FROM `core_homes`").executeQuery();
            while(rs.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(rs.getString("owner"))).peek(user -> {
                    try {
                        Home home = new Home(user, rs.getString("name"), LocationUtil.locationFromString(rs.getString("location")));
                        user.addHome(home);
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUUID(UUID oldUUID, UUID newUUID){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_homes` SET `owner` = '"+newUUID.toString()+"' WHERE `owner` = '"+oldUUID.toString()+"';").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertHome(Home home){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_homes` (id, owner, name, location) VALUES ("+
                    "?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, home.getOwner().getUuid().toString());
            statement.setString(3, home.getName());
            statement.setString(4, LocationUtil.locationToString(home.getLocation()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(Home home){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_homes` WHERE `owner` = '"+home.getOwner().getUuid().toString()+"' AND `name` = '"+home.getName()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Home home){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_homes` SET `location` = '"+LocationUtil.locationToString(home.getLocation())+"' "+
                    "WHERE `owner` = '"+home.getOwner().getUuid().toString()+"' AND `name` = '"+home.getName()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
