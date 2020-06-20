package pl.fernikq.core.guild.drill;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildDrillData {

    private final CorePlugin plugin;

    public GuildDrillData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadDrills();
    }

    private void checkTable(){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guild_drills` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT, "+
                    "`guild` TEXT NOT NULL, "+
                    "`location` TEXT NOT NULL, "+
                    "`inventory` TEXT NOT NULL, "+
                    "`material` TEXT NOT NULL, "+
                    "`level` INT NOT NULL);").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private void loadDrills(){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guild_drills`").executeQuery();
            while(resultSet.next()){
                this.plugin.getGuildManager().getGuildByTag(resultSet.getString("guild")).peek(guild -> {
                    GuildDrill guildDrill = new GuildDrill(guild, resultSet);
                    guild.addDrill(guildDrill);
                    this.plugin.getDrillManager().registerDrillTask(guildDrill);
                });
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insert(GuildDrill guildDrill){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `core_guild_drills` (id, guild, location, inventory, material, level)  VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, guildDrill.getGuild().getTag());
            preparedStatement.setString(3, LocationUtil.locationToString(guildDrill.getCenter()));
            preparedStatement.setString(4, SerializationUtil.itemStackToString(guildDrill.getInventory().getContents()));
            preparedStatement.setString(5, guildDrill.getMaterial().name());
            preparedStatement.setInt(6, guildDrill.getLevel());
            preparedStatement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void update(GuildDrill guildDrill){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `core_guild_drills` SET `inventory` = ?, `level` = ?, `material` = ? WHERE `guild` = '"+guildDrill.getGuild().getTag()+"' AND `location` = '"+LocationUtil.locationToString(guildDrill.getCenter())+"'");
            preparedStatement.setString(1, SerializationUtil.itemStackToString(guildDrill.getInventory().getContents()));
            preparedStatement.setInt(2, guildDrill.getLevel());
            preparedStatement.setString(3, guildDrill.getMaterial().name());
            preparedStatement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void delete(GuildDrill guildDrill){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_guild_drills` WHERE `guild` = '"+guildDrill.getGuild().getTag()+"' AND `location` = '"+LocationUtil.locationToString(guildDrill.getCenter())+"'").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
