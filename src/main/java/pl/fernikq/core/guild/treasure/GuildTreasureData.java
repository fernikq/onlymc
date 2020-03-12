package pl.fernikq.core.guild.treasure;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildTreasureData {

    private final CorePlugin plugin;

    public GuildTreasureData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadTreasures();
    }

    public void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guild_treasures` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`guild` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`level` INT NOT NULL,"+
                    "`coins` INT NOT NULL,"+
                    "`items` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTreasures(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guild_treasures`").executeQuery();
            while(resultSet.next()){
                this.plugin.getGuildManager().getGuildByTag(resultSet.getString("guild")).peek(guild -> {
                    new GuildTreasure(guild, resultSet);
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertTreasure(GuildTreasure treasure){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_guild_treasures` ("+
                    "id, guild, level, coins, items) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, treasure.getGuild().getTag());
            statement.setInt(3, treasure.getLevel());
            statement.setInt(4, treasure.getCoins());
            statement.setString(5, SerializationUtil.itemStackToString(treasure.getItems()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTreasure(GuildTreasure treasure){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE `core_guild_treasures` SET `level` = ?, "+
                    "`coins` = ?, `items` = ? WHERE `guild` = '"+treasure.getGuild().getTag()+"';");
            statement.setInt(1, treasure.getLevel());
            statement.setInt(2, treasure.getCoins());
            statement.setString(3, SerializationUtil.itemStackToString(treasure.getItems()));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTreasure(GuildTreasure treasure){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_guild_treasures` WHERE `guild` = '"+treasure.getGuild().getTag()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
