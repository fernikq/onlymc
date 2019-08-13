package pl.fernikq.core.user;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.Logger;

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
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_users` ("+
                    "`uuid` VARCHAR(128) PRIMARY KEY UNIQUE,"+
                    "`name` VARCHAR(32) UNIQUE,"+
                    "`firstAddress` TEXT,"+
                    "`lastAddress` TEXT,"+
                    "`groupName` TEXT);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers(){
        try {
            ResultSet rs = this.plugin.getMySQL().query("SELECT * FROM `core_users`");
            while(rs.next()){
                User user = new User(rs);
                this.plugin.getUserManager().registerUser(user);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(User user){
        try {
            final PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_users` "+
                    "(uuid, name, firstAddress, lastAddress, groupName) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, user.getName());
            statement.setString(3, user.getFirstAddress());
            statement.setString(4, user.getLastAddress());
            statement.setString(5, user.getGroup().name());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user){
        try {
            final PreparedStatement statement = this.plugin.getMySQL().generateStatement("UPDATE `core_users` SET `name` = ?, `lastAddress` = ?, "+
                    "`groupName` = ? WHERE `uuid` = '"+user.getUuid().toString()+"';");
            statement.setString(1, user.getName());
            statement.setString(2, user.getLastAddress());
            statement.setString(3, user.getGroup().name());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
