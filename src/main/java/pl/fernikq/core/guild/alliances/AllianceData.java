package pl.fernikq.core.guild.alliances;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllianceData {

    private final CorePlugin plugin;

    public AllianceData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadAlliances();
    }

    public void checkTable(){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_alliances` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`guild1` TEXT NOT NULL,"+
                    "`guild2` TEXT NOT NULL);");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAlliances(){
        try {
            this.plugin.getMySQL().openConnection();
            ResultSet resultSet = this.plugin.getMySQL().query("SELECT * FROM `core_alliances`");
            while(resultSet.next()){
                String guild1Tag = resultSet.getString("guild1");
                String guild2Tag = resultSet.getString("guild2");
                this.plugin.getGuildManager().getGuildByTag(guild1Tag).peek(guild1 -> {
                   this.plugin.getGuildManager().getGuildByTag(guild2Tag).peek(guild2 -> {
                       this.plugin.getAllianceManager().registerAlliance(guild1, guild2);
                   });
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAlliance(Alliance alliance){
        try {
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_alliances` ("+
                    "id, guild1, guild2) VALUES (?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, alliance.getGuild1().getTag());
            statement.setString(3, alliance.getGuild2().getTag());
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteAlliance(Alliance alliance){
        try {
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_alliances` WHERE `guild1` = '"+alliance.getGuild1().getTag()+"' AND `guild2` = '"+alliance.getGuild2().getTag()+"' "+
                    "OR `guild1` = '"+alliance.getGuild2().getTag()+"' AND `guild2` = '"+alliance.getGuild1().getTag()+"';");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
