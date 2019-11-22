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
                    "`depositeEnchantedApples` INT NOT NULL,"+
                    "`minedStone` INT NOT NULL,"+
                    "`miningExperience` INT NOT NULL,"+
                    "`openedCobblex` INT NOT NULL,"+
                    "`openedPremiumCase` INT NOT NULL,"+
                    "`coinsFromStone` INT NOT NULL,"+
                    "`turboDropTime` LONG NOT NULL,"+
                    "`turboExpTime` LONG NOT NULL,"+
                    "`points` INT NOT NULL,"+
                    "`kills` INT NOT NULL,"+
                    "`deaths` INT NOT NULL,"+
                    "`assists` INT NOT NULL);");
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
                   new UserStat(user, resultSet);
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
                    "(id, uuid, coins, level, depositePearls, depositeApples, depositeEnchantedApples, minedStone, miningExperience, openedCobblex, openedPremiumCase, coinsFromStone, turboDropTime, " +
                    "turboExpTime, points, kills, deaths, assists)"+
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, user.getUuid().toString());
            statement.setInt(3, stat.getCoins());
            statement.setInt(4, stat.getLevel());
            statement.setInt(5, stat.getDepositePearls());
            statement.setInt(6, stat.getDepositeApples());
            statement.setInt(7, stat.getDepositeEnchantedApples());
            statement.setInt(8, stat.getMinedStone());
            statement.setInt(9, stat.getMiningExperience());
            statement.setInt(10, stat.getOpenedCobblex());
            statement.setInt(11, stat.getOpenedPremiumCase());
            statement.setInt(12, stat.getCoinsFromStone());
            statement.setLong(13, stat.getTurboDropTime());
            statement.setLong(14, stat.getTurboExpTime());
            statement.setInt(15, stat.getPoints());
            statement.setInt(16, stat.getKills());
            statement.setInt(17, stat.getDeaths());
            statement.setInt(18, stat.getAssists());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStats(User user){
        try {
            if(!this.plugin.getMySQL().isConnected()){
                this.plugin.getMySQL().openConnection();
            }
            UserStat stat = user.getUserStat();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("UPDATE `core_user_stats` SET "+
                    "`coins` = ?, `level` = ?, `depositePearls` = ?, `depositeApples` = ?, `depositeEnchantedApples` = ?, "+
                    "`minedStone` = ?, `miningExperience` = ?, `openedCobblex` = ?, `openedPremiumCase` = ?, `coinsFromStone` = ?, `turboDropTime` = ?, `turboExpTime` = ?, "+
                    "`points` = ?, `kills` = ?, `deaths` = ?, `assists` = ? "+
                    "WHERE `uuid` = '"+user.getUuid().toString()+"';");
            statement.setInt(1, stat.getCoins());
            statement.setInt(2, stat.getLevel());
            statement.setInt(3, stat.getDepositePearls());
            statement.setInt(4, stat.getDepositeApples());
            statement.setInt(5, stat.getDepositeEnchantedApples());
            statement.setInt(6, stat.getMinedStone());
            statement.setInt(7, stat.getMiningExperience());
            statement.setInt(8, stat.getOpenedCobblex());
            statement.setInt(9, stat.getOpenedPremiumCase());
            statement.setInt(10, stat.getCoinsFromStone());
            statement.setLong(11, stat.getTurboDropTime());
            statement.setLong(12, stat.getTurboExpTime());
            statement.setInt(13, stat.getPoints());
            statement.setInt(14, stat.getKills());
            statement.setInt(15, stat.getDeaths());
            statement.setInt(16, stat.getAssists());
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
