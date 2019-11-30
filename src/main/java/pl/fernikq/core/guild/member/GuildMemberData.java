package pl.fernikq.core.guild.member;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;

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
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_guild_members` ("+
                    "`id` INT(16) PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL UNIQUE,"+
                    "`guild` TEXT NOT NULL,"+
                    "`permissions` TEXT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMembers(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet resultSet = this.plugin.getMySQL().query("SELECT * FROM `core_guild_members`");
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

    public void insertMember(GuildMember member){
        try {
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_guild_members` (id, uuid, guild, permissions) "+
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
        try{
            if(!this.plugin.getMySQL().isConnected()){
                this.plugin.getMySQL().openConnection();
            }
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("UPDATE `core_guild_members` SET `permissions` = ? WHERE `uuid` = '"+member.getUser().getUuid().toString()+"';");
            statement.setString(1, GuildPermission.getMemberPermissionsToString(member));
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteMember(GuildMember member){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_guild_members` WHERE `uuid` = '"+member.getUser().getUuid().toString()+"' AND `guild` = '"+member.getGuild().getTag()+"'");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
