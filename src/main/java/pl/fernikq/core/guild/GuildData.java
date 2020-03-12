package pl.fernikq.core.guild;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GuildData {


    private final CorePlugin plugin;

    public GuildData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadGuilds();
    }

    public void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guilds` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`tag` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`name` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`owner` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`creationTime` LONG NOT NULL,"+
                    "`lastAttackTime` LONG NOT NULL,"+
                    "`maxMembers` INT NOT NULL,"+
                    "`maxAllies` INT NOT NULL,"+
                    "`expireTime` LONG NOT NULL,"+
                    "`health` INT NOT NULL,"+
                    "`enlargeAlliesLevel` INT NOT NULL,"+
                    "`enlargeMembersLevel` INT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadGuilds(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guilds`").executeQuery();
            while(resultSet.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(resultSet.getString("owner"))).peek(user -> {
                   Guild guild = new Guild(user, resultSet);
                   this.plugin.getGuildManager().registerGuild(guild);
                   this.plugin.getTopManager().getTopsByKind(TopKind.GUILD).forEach(sortable -> sortable.addObject(guild));
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUUID(UUID oldUUID, UUID newUUID){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_guilds` SET `owner` = '"+newUUID.toString()+"' WHERE `owner` = '"+oldUUID.toString()+"';").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertGuild(Guild guild){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_guilds` ("+
                    "id, tag, name, owner, creationTime, lastAttackTime, maxMembers, maxAllies, expireTime, health, enlargeAlliesLevel, enlargeMembersLevel) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, guild.getTag());
            statement.setString(3, guild.getName());
            statement.setString(4, guild.getOwner().getUuid().toString());
            statement.setLong(5, guild.getCreationTime());
            statement.setLong(6, guild.getLastAttackTime());
            statement.setInt(7, guild.getMaxMembers());
            statement.setInt(8, guild.getMaxAllies());
            statement.setLong(9, guild.getExpireTime());
            statement.setInt(10, guild.getHealth());
            statement.setInt(11, guild.getEnlargeAlliesLevel());
            statement.setInt(12, guild.getEnlargeMembersLevel());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGuild(Guild guild){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE `core_guilds` SET `owner` = ?, `lastAttackTime` = ?, "+
                    "`maxMembers` = ?, `maxAllies` = ?, `expireTime` = ?, `health` = ?, `enlargeAlliesLevel` = ?, `enlargeMembersLevel` = ? "+
                    "WHERE `tag` = '"+guild.getTag()+"';");
            statement.setString(1, guild.getOwner().getUuid().toString());
            statement.setLong(2, guild.getLastAttackTime());
            statement.setInt(3, guild.getMaxMembers());
            statement.setInt(4, guild.getMaxAllies());
            statement.setLong(5, guild.getExpireTime());
            statement.setInt(6, guild.getHealth());
            statement.setInt(7, guild.getEnlargeAlliesLevel());
            statement.setInt(8, guild.getEnlargeMembersLevel());
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGuild(Guild guild){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_guilds` WHERE `tag` = '"+guild.getTag()+"';").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
