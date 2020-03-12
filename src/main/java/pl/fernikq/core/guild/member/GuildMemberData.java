package pl.fernikq.core.guild.member;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class GuildMemberData {

    private final CorePlugin plugin;

    public GuildMemberData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadMembers();
    }

    private void checkTable(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_guild_members` ("+
                    "`id` INT(16) PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`guild` TEXT NOT NULL,"+
                    "`permissions` TEXT NOT NULL);").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMembers(){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_guild_members`").executeQuery();
            while(resultSet.next()){
                String tag = resultSet.getString("guild");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                List<GuildPermission> guildPermissions = GuildPermission.getMemberPermissionsFromString(resultSet.getString("permissions"));
                this.plugin.getUserManager().getUser(uuid).peek(user -> {
                   this.plugin.getGuildManager().getGuildByTag(tag).peek(guild -> {
                       new GuildMember(user, guild, guildPermissions);
                   });
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUUID(UUID oldUUID, UUID newUUID){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("UPDATE `core_guild_members` SET `uuid` = '"+newUUID.toString()+"' WHERE `uuid` = '"+oldUUID.toString()+"';").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertMember(GuildMember member){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `core_guild_members` (id, uuid, guild, permissions) "+
                    "VALUES (?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, member.getUser().getUuid().toString());
            statement.setString(3, member.getGuild().getTag());
            statement.setString(4, GuildPermission.getMemberPermissionsToString(member));
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMember(GuildMember member){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE `core_guild_members` SET `permissions` = ? WHERE `uuid` = '"+member.getUser().getUuid().toString()+"';");
            statement.setString(1, GuildPermission.getMemberPermissionsToString(member));
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteMember(GuildMember member){
        try (Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("DELETE FROM `core_guild_members` WHERE `uuid` = '"+member.getUser().getUuid().toString()+"' AND `guild` = '"+member.getGuild().getTag()+"'").executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
