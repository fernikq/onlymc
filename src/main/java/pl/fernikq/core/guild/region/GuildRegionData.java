package pl.fernikq.core.guild.region;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildRegionData {

    private final CorePlugin plugin;

    public GuildRegionData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadRegions();
    }

    public void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guild_regions` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`guild` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`size` INT NOT NULL,"+
                    "`explodeProtectionTime` LONG NOT NULL,"+
                    "`home` TEXT NOT NULL,"+
                    "`center` TEXT NOT NULL,"+
                    "`enlargeRegionLevel` INT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRegions(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guild_regions`").executeQuery();
            while(resultSet.next()){
                this.plugin.getGuildManager().getGuildByTag(resultSet.getString("guild")).peek(guild -> {
                   new GuildRegion(guild, resultSet);
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRegion(GuildRegion region){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_guild_regions` ("+
                    "id, guild, size, explodeProtectionTime, home, center, enlargeRegionLevel) VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, region.getGuild().getTag());
            statement.setInt(3, region.getSize());
            statement.setLong(4, region.getExplodeProtectionTime());
            statement.setString(5, LocationUtil.locationToString(region.getHome()));
            statement.setString(6, LocationUtil.locationToString(region.getCenter()));
            statement.setInt(7, region.getEnlargeRegionLevel());
            statement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();;
        }
    }

    public void updateRegion(GuildRegion region){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE `core_guild_regions` SET `size` = ?, `explodeProtectionTime` = ?, "+
                    "`home` = ?, `center` = ?, `enlargeRegionLevel` = ? WHERE `guild` = '"+region.getGuild().getTag()+"';");
            statement.setInt(1, region.getSize());
            statement.setLong(2, region.getExplodeProtectionTime());
            statement.setString(3, LocationUtil.locationToString(region.getHome()));
            statement.setString(4, LocationUtil.locationToString(region.getCenter()));
            statement.setInt(5, region.getEnlargeRegionLevel());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRegion(GuildRegion region){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_guild_regions` WHERE `guild` = '"+region.getGuild().getTag()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
