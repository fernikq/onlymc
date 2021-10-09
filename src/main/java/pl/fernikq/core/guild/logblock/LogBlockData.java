package pl.fernikq.core.guild.logblock;

import org.bukkit.Location;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.LocationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LogBlockData {

    private final CorePlugin plugin;

    public LogBlockData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadLogblocks();
    }

    private void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guild_logblocks` ("+
                    "`id` INT(16) PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`location` TEXT NOT NULL,"+
                    "`guild` TEXT NOT NULL,"+
                    "`time` LONG NOT NULL, " +
                    "`userName` TEXT NOT NULL," +
                    "`actionType` TEXT NOT NULL," +
                    "`blockType` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadLogblocks(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guild_logblocks`;").executeQuery();
            while(resultSet.next()){
                String tag = resultSet.getString("guild");
                Location location = LocationUtil.locationFromString(resultSet.getString("location"));
                long time = resultSet.getLong("time");
                String userName = resultSet.getString("userName");
                String blockType = resultSet.getString("blockType");
                LogBlockActionType actionType = LogBlockActionType.getLogBlockActionTypeByName(resultSet.getString("actionType")).getOrNull();
                if(Objects.isNull(actionType)){
                    continue;
                }
                this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
                    LogBlock logBlock = new LogBlock(location, time, userName, actionType, blockType);
                    guild.addLogBlockAtLocation(logBlock);
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertLogBlock(LogBlock logBlock, Guild guild){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_guild_logblocks` (id, location, guild, time, userName, actionType, blockType) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, LocationUtil.locationToString(logBlock.getLocation()));
            statement.setString(3, guild.getTag());
            statement.setLong(4, logBlock.getTime());
            statement.setString(5, logBlock.getUserName());
            statement.setString(6, logBlock.getLogBlockActionType().name());
            statement.setString(7, logBlock.getBlockType());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
