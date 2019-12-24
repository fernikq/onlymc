package pl.fernikq.core.user;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.enderchest.Enderchest;
import pl.fernikq.core.util.Logger;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {

    private final CorePlugin plugin;

    public UserData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadUsers();
    }

    private void checkTable(){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_users` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`name` VARCHAR(32) NOT NULL UNIQUE,"+
                    "`firstAddress` TEXT NOT NULL,"+
                    "`lastAddress` TEXT NOT NULL,"+
                    "`groupName` TEXT NOT NULL,"+
                    "`kitTimes` TEXT NOT NULL,"+
                    "`enderchestItems` TEXT NOT NULL,"+
                    "`enderchestLevel` INT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet rs = this.plugin.getMySQL().query("SELECT * FROM `core_users`");
            while(rs.next()){
                User user = new User(rs);
                user.setKitTimes(this.plugin.getKitManager().kitsFromString(rs.getString("kitTimes")));
                new Enderchest(user, rs);
                this.plugin.getUserManager().registerUser(user);
                this.plugin.getTopManager().getTopsByKind(TopKind.USER).forEach(sortable -> sortable.addObject(user));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(User user){
        try {
            this.plugin.getMySQL().openConnection();
            final PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_users` "+
                    "(id, uuid, name, firstAddress, lastAddress, groupName, kitTimes, enderchestItems, enderchestLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, user.getUuid().toString());
            statement.setString(3, user.getName());
            statement.setString(4, user.getFirstAddress());
            statement.setString(5, user.getLastAddress());
            statement.setString(6, user.getGroup().name());
            statement.setString(7, this.plugin.getKitManager().kitsToString(user.getKitTimes()));
            statement.setString(8, SerializationUtil.itemStackToString(user.getEnderchest().getItems()));
            statement.setInt(9, user.getEnderchest().getLevel());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user){
        try {
            if(!this.plugin.getMySQL().isConnected()){
                this.plugin.getMySQL().openConnection();
            }
            final PreparedStatement statement = this.plugin.getMySQL().generateStatement("UPDATE `core_users` SET `uuid` = ?, `name` = ?, `lastAddress` = ?, "+
                    "`groupName` = ?, `kitTimes` = ?, enderchestItems = ?, enderchestLevel = ? WHERE `uuid` = '"+user.getUuid().toString()+"' OR `name` = '"+user.getName()+"';");
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, user.getName());
            statement.setString(3, user.getLastAddress());
            statement.setString(4, user.getGroup().name());
            statement.setString(5, this.plugin.getKitManager().kitsToString(user.getKitTimes()));
            statement.setString(6, SerializationUtil.itemStackToString(user.getEnderchest().getItems()));
            statement.setInt(7, user.getEnderchest().getLevel());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_users` WHERE `uuid` = '"+user.getUuid().toString()+"';");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
