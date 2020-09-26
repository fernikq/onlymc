package pl.fernikq.core.magiccase;

import org.bukkit.Location;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.LocationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MagicCaseData {

    private final CorePlugin plugin;

    public MagicCaseData(CorePlugin plugin){
        this.plugin = plugin;
        checkTable();
        loadData();
    }

    private void checkTable(){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `core_magic_case` ("+
                    "`id` INT(32) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT, "+
                    "`location` TEXT NOT NULL, "+
                    "`caseType` TEXT NOT NULL);").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private void loadData(){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `core_magic_case`").executeQuery();
            while(resultSet.next()){
                MagicCaseType magicCaseType = MagicCaseType.valueOf(resultSet.getString("caseType"));
                this.plugin.getMagicCaseManager().addCase(LocationUtil.locationFromString(resultSet.getString("location")), new MagicCase(magicCaseType));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void saveMagicCase(Location location, MagicCaseType magicCaseType){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `core_magic_case` (id, location, caseType) VALUES (?, ?, ?)");
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, LocationUtil.locationToString(location));
            preparedStatement.setString(3, magicCaseType.name());
            preparedStatement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void deleteMagicCase(Location location, MagicCaseType magicCaseType){
        try(Connection connection = this.plugin.getMySQL().getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `core_magic_case` WHERE `location` = ? AND `caseType` = ?");
            preparedStatement.setString(1, LocationUtil.locationToString(location));
            preparedStatement.setString(2, magicCaseType.name());
            preparedStatement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
