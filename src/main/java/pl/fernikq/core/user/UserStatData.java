package pl.fernikq.core.user;

import org.omg.CORBA.MARSHAL;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.magiccase.MagicCaseType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserStatData {

    private final CorePlugin plugin;

    public UserStatData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadStats();
    }

    private void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_user_stats` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`coins` INT NOT NULL,"+
                    "`level` INT NOT NULL,"+
                    "`depositePearls` INT NOT NULL,"+
                    "`depositeApples` INT NOT NULL,"+
                    "`depositeEnchantedApples` INT NOT NULL,"+
                    "`depositeArrows` INT NOT NULL,"+
                    "`depositeSnowballs` INT NOT NULL,"+
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
                    "`assists` INT NOT NULL,"+
                    "`distanceTraveled` INT NOT NULL,"+
                    "`logouts` INT NOT NULL,"+
                    "`spentTime` LONG NOT NULL,"+
                    "`exploredGuilds` TEXT NOT NULL,"+
                    "`killedUsers` TEXT NOT NULL,"+
                    "`killedWithRankUsers` TEXT NOT NULL,"+
                    "`comebackDaysInRow` INT NOT NULL,"+
                    "`comebackDay` INT NOT NULL,"+
                    "`minedWood` INT NOT NULL,"+
                    "`catchedFishes` INT NOT NULL,"+
                    "`timeAwardAmount` INT NOT NULL,"+
                    "`comebackAwardAmount` INT NOT NULL,"+
                    "`killWithRankAwardAmount` INT NOT NULL,"+
                    "`killedUsersAwardAmount` INT NOT NULL,"+
                    "`exploredGuildsAwardAmount` INT NOT NULL,"+
                    "`keyFragments` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStats(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_user_stats`").executeQuery();
            while(resultSet.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(resultSet.getString("uuid"))).peek(user -> {
                   UserStat userStat = new UserStat(user, resultSet);
                    try {
                        Arrays.stream(resultSet.getString("keyFragments").split(";")).filter(s -> !s.isEmpty()).forEach(s -> {
                            String[] data = s.split(":");
                            userStat.addKeyFragmentsByMagicCaseType(MagicCaseType.getMagicCaseTypeByName(data[0]), Integer.parseInt(data[1]));
                        });
                        Arrays.stream(resultSet.getString("exploredGuilds").split(":")).filter(s -> !s.isEmpty()).forEach(s -> userStat.getExploredGuilds().add(s));
                        Arrays.stream(resultSet.getString("killedUsers").split(":")).filter(s -> this.plugin.getUserManager().getUser(s).isDefined()).forEach(s -> userStat.getKilledUsers().add(this.plugin.getUserManager().getUser(s).get()));
                        Arrays.stream(resultSet.getString("killedWithRankUsers").split(":")).filter(s -> this.plugin.getUserManager().getUser(s).isDefined()).forEach(s -> userStat.getKilledWithRankUsers().add(this.plugin.getUserManager().getUser(s).get()));
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStats(User user){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            UserStat stat = user.getUserStat();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_user_stats` "+
                    "(id, uuid, coins, level, depositePearls, depositeApples, depositeEnchantedApples, depositeArrows, depositeSnowballs, minedStone, miningExperience, openedCobblex, openedPremiumCase, coinsFromStone, turboDropTime, " +
                    "turboExpTime, points, kills, deaths, assists, distanceTraveled, logouts, spentTime, exploredGuilds, killedUsers, killedWithRankUsers, comebackDaysInRow, comebackDay, minedWood, catchedFishes, timeAwardAmount, comebackAwardAmount, " +
                    "killWithRankAwardAmount, killedUsersAwardAmount, exploredGuildsAwardAmount, keyFragments)"+
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, user.getUuid().toString());
            statement.setInt(3, stat.getCoins());
            statement.setInt(4, stat.getLevel());
            statement.setInt(5, stat.getDepositePearls());
            statement.setInt(6, stat.getDepositeApples());
            statement.setInt(7, stat.getDepositeEnchantedApples());
            statement.setInt(8, stat.getDepositeArrows());
            statement.setInt(9, stat.getDepositeSnowballs());
            statement.setInt(10, stat.getMinedStone());
            statement.setInt(11, stat.getMiningExperience());
            statement.setInt(12, stat.getOpenedCobblex());
            statement.setInt(13, stat.getOpenedPremiumCase());
            statement.setInt(14, stat.getCoinsFromStone());
            statement.setLong(15, stat.getTurboDropTime());
            statement.setLong(16, stat.getTurboExpTime());
            statement.setInt(17, stat.getPoints());
            statement.setInt(18, stat.getKills());
            statement.setInt(19, stat.getDeaths());
            statement.setInt(20, stat.getAssists());
            statement.setInt(21, stat.getDistanceTraveled());
            statement.setInt(22, stat.getLogouts());
            statement.setLong(23, stat.getSpentTime());
            String string = String.join(":", stat.getExploredGuilds());
            statement.setString(24, string);
            string = String.join(":", this.plugin.getUserManager().getUsersNames(stat.getKilledUsers().stream().collect(Collectors.toList())));
            statement.setString(25, string);
            string = String.join(":", this.plugin.getUserManager().getUsersNames(stat.getKilledWithRankUsers().stream().collect(Collectors.toList())));
            statement.setString(26, string);
            statement.setInt(27, stat.getComebackDaysInRow());
            statement.setInt(28, stat.getComebackDay());
            statement.setInt(29, stat.getMinedWood());
            statement.setInt(30, stat.getCatchedFishes());
            statement.setInt(31, stat.getTimeAwardAmount());
            statement.setInt(32, stat.getComebackAwardAmount());
            statement.setInt(33, stat.getKillWithRankAwardAmount());
            statement.setInt(34, stat.getKilledUsersAwardAmount());
            statement.setInt(35, stat.getExploredGuildsAwardAmount());
            statement.setString(36, this.getKeyFragmentsToString(stat.getKeyFragments()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private String getKeyFragmentsToString(Map<MagicCaseType, Integer> caseMap){
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for(Map.Entry map : caseMap.entrySet()){
            if(i == 0) {
                stringBuilder.append(((MagicCaseType)map.getKey()).name() + ":" + Integer.toString((int)map.getValue()));
            }else {
                stringBuilder.append(";" + ((MagicCaseType)map.getKey()).name() + ":" + Integer.toString((int)map.getValue()));
            }
            i++;
        }
        return stringBuilder.toString();
    }

    public void updateUUID(UUID oldUUID, UUID newUUID){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_user_stats` SET `uuid` = '"+newUUID.toString()+"' WHERE `uuid` = '"+oldUUID.toString()+"';").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void updateStats(User user){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            UserStat stat = user.getUserStat();
            PreparedStatement statement = connection.prepareStatement("UPDATE `core_user_stats` SET "+
                    "`coins` = ?, `level` = ?, `depositePearls` = ?, `depositeApples` = ?, `depositeEnchantedApples` = ?, `depositeArrows` = ?, `depositeSnowballs` = ?, "+
                    "`minedStone` = ?, `miningExperience` = ?, `openedCobblex` = ?, `openedPremiumCase` = ?, `coinsFromStone` = ?, `turboDropTime` = ?, `turboExpTime` = ?, "+
                    "`points` = ?, `kills` = ?, `deaths` = ?, `assists` = ?, `distanceTraveled` = ?, `logouts` = ?, `spentTime` = ?, `exploredGuilds` = ?, `killedUsers` = ?, `killedWithRankUsers` = ?, "+
                    "`comebackDaysInRow` = ?, `comebackDay` = ?, `minedWood` = ?, `catchedFishes` = ?, `timeAwardAmount` = ?, `comebackAwardAmount` = ?, "+
                    "`killWithRankAwardAmount` = ?, `killedUsersAwardAmount` = ?, `exploredGuildsAwardAmount` = ?, `keyFragments` = ? WHERE `uuid` = '"+user.getUuid().toString()+"';");
            statement.setInt(1, stat.getCoins());
            statement.setInt(2, stat.getLevel());
            statement.setInt(3, stat.getDepositePearls());
            statement.setInt(4, stat.getDepositeApples());
            statement.setInt(5, stat.getDepositeEnchantedApples());
            statement.setInt(6, stat.getDepositeArrows());
            statement.setInt(7, stat.getDepositeSnowballs());
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
            statement.setInt(19, stat.getDistanceTraveled());
            statement.setInt(20, stat.getLogouts());
            statement.setLong(21, stat.getSpentTime());
            String string = String.join(":", stat.getExploredGuilds());
            statement.setString(22, string);
            string = String.join(":", this.plugin.getUserManager().getUsersNames(stat.getKilledUsers().stream().collect(Collectors.toList())));
            statement.setString(23, string);
            string = String.join(":", this.plugin.getUserManager().getUsersNames(stat.getKilledWithRankUsers().stream().collect(Collectors.toList())));
            statement.setString(24, string);
            statement.setInt(25, stat.getComebackDaysInRow());
            statement.setInt(26, stat.getComebackDay());
            statement.setInt(27, stat.getMinedWood());
            statement.setInt(28, stat.getCatchedFishes());
            statement.setInt(29, stat.getTimeAwardAmount());
            statement.setInt(30, stat.getComebackAwardAmount());
            statement.setInt(31, stat.getKillWithRankAwardAmount());
            statement.setInt(32, stat.getKilledUsersAwardAmount());
            statement.setInt(33, stat.getExploredGuildsAwardAmount());
            statement.setString(34, this.getKeyFragmentsToString(stat.getKeyFragments()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_user_stats` WHERE `uuid` = '"+user.getUuid().toString()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
