package pl.fernikq.core.user;

import pl.fernikq.core.CorePlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserStatData {

    private final CorePlugin plugin;

    public UserStatData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadStats();
    }

    private void checkTable(){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_user_stats` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`coins` INT NOT NULL,"+
                    "`level` INT NOT NULL,"+
                    "`depositePearls` INT NOT NULL,"+
                    "`depositeApples` INT NOT NULL,"+
                    "`depositeEnchantedApples` INT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStats(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet resultSet = this.plugin.getMySQL().query("SELECT * FROM `core_user_stats`");
            while(resultSet.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(resultSet.getString("uuid"))).peek(user -> {
                   UserStat userStat = new UserStat(user, resultSet);
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStats(User user){
        try {
            UserStat stat = user.getUserStat();
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_user_stats` "+
                    "(id, uuid, coins, level, depositePearls, depositeApples, depositeEnchantedApples) VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, user.getUuid().toString());
            statement.setInt(3, stat.getCoins());
            statement.setInt(4, stat.getLevel());
            statement.setInt(5, stat.getDepositePearls());
            statement.setInt(6, stat.getDepositeApples());
            statement.setInt(7, stat.getDepositeEnchantedApples());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStats(User user){
        try {
            UserStat stat = user.getUserStat();
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("UPDATE `core_user_stats` SET "+
                    "`coins` = ?, `level` = ?, `depositePearls` = ?, `depositeApples` = ?, `depositeEnchantedApples` = ? "+
                    "WHERE `uuid` = '"+user.getUuid().toString()+"';");
            statement.setInt(1, stat.getCoins());
            statement.setInt(2, stat.getLevel());
            statement.setInt(3, stat.getDepositePearls());
            statement.setInt(4, stat.getDepositeApples());
            statement.setInt(5, stat.getDepositeEnchantedApples());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_user_stats` WHERE `uuid` = '"+user.getUuid().toString()+"';");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
