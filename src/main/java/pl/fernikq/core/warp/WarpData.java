package pl.fernikq.core.warp;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarpData {

    private final CorePlugin plugin;

    public WarpData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadWarps();
    }

    public void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_warps` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`name` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`location` TEXT NOT NULL,"+
                    "`requiredGroup` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadWarps(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_warps`").executeQuery();
            while(resultSet.next()){
                Warp warp = new Warp(resultSet);
                this.plugin.getWarpManager().registerWarp(warp);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertWarp(Warp warp){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `core_warps` (id, name, location, requiredGroup) VALUES (?, ?, ?, ?)");
            statement.setString(1, null);
            statement.setString(2, warp.getName());
            statement.setString(3, LocationUtil.locationToString(warp.getLocation()));
            statement.setString(4, warp.getRequiredGroup().name());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteWarp(Warp warp){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_warps` WHERE `name` = '"+warp.getName()+"'").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWarp(Warp warp){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_warps` SET `location` = '"+LocationUtil.locationToString(warp.getLocation())+"', `requiredGroup` = '"+warp.getRequiredGroup().name()+"' WHERE `name` = '"+warp.getName()+"'").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
