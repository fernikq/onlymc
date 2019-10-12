package pl.fernikq.core.warp;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

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
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_warps` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`name` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`location` TEXT NOT NULL,"+
                    "`requiredGroup` TEXT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadWarps(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet resultSet = this.plugin.getMySQL().query("SELECT * FROM `core_warps`");
            while(resultSet.next()){
                Warp warp = new Warp(resultSet);
                this.plugin.getWarpManager().registerWarp(warp);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertWarp(Warp warp){
        try {
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement(
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
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_warps` WHERE `name` = '"+warp.getName()+"'");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWarp(Warp warp){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("UPDATE `core_warps` SET `location` = '"+LocationUtil.locationToString(warp.getLocation())+"', `requiredGroup` = '"+warp.getRequiredGroup().name()+"' WHERE `name` = '"+warp.getName()+"'");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
