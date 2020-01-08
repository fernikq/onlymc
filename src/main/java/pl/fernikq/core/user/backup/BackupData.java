package pl.fernikq.core.user.backup;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BackupData {

    private final CorePlugin plugin;

    public BackupData(CorePlugin plugin) {
        this.plugin = plugin;
        checkTable();
        loadBackups();
    }

    private void checkTable(){
        try{
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("CREATE TABLE IF NOT EXISTS `core_user_backups` ("+
                    "`id` INT(16) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,"+
                    "`uuid` VARCHAR(128) NOT NULL,"+
                    "`items` TEXT NOT NULL,"+
                    "`armor` TEXT NOT NULL,"+
                    "`points` INT NOT NULL,"+
                    "`deaths` INT NOT NULL,"+
                    "`creationTime` LONG NOT NULL,"+
                    "`reason` TEXT NOT NULL,"+
                    "`ping` INT NOT NULL);");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private void loadBackups(){
        try{
            this.plugin.getMySQL().openConnection();
            ResultSet resultSet = this.plugin.getMySQL().query("SELECT * FROM `core_user_backups`");
            while(resultSet.next()){
                this.plugin.getUserManager().getUser(UUID.fromString(resultSet.getString("uuid"))).peek(user -> {
                   new Backup(user, resultSet);
                });
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void deleteBackup(Backup backup){
        try{
            this.plugin.getMySQL().openConnection();
            this.plugin.getMySQL().update("DELETE FROM `core_user_backups` WHERE `uuid` = '"+backup.getUser().getUuid().toString()+"';");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertBackup(Backup backup){
        try{
            this.plugin.getMySQL().openConnection();
            PreparedStatement statement = this.plugin.getMySQL().generateStatement("INSERT INTO `core_user_backups` (id, uuid, items, armor, points, deaths, creationTime, reason, ping) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, null);
            statement.setString(2, backup.getUser().getUuid().toString());
            statement.setString(3, SerializationUtil.itemStackToString(backup.getItems()));
            statement.setString(4, SerializationUtil.itemStackToString(backup.getArmor()));
            statement.setInt(5, backup.getPoints());
            statement.setInt(6, backup.getDeaths());
            statement.setLong(7, backup.getDeathTime());
            statement.setString(8, backup.getReason());
            statement.setInt(9, backup.getPing());
            statement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
