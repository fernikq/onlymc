package pl.fernikq.core.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private final CorePlugin plugin;
    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;
    private Connection connection;

    public MySQL(CorePlugin plugin){
        this.plugin = plugin;
        this.hikariConfig = new HikariConfig();
        this.hikariConfig.setJdbcUrl("jdbc:mysql://"+ConfigManager.mysqlHost+":"+ConfigManager.mysqlPort+"/"+ConfigManager.mysqlBase);
        this.hikariConfig.setUsername(ConfigManager.mysqlUser);
        this.hikariConfig.setPassword(ConfigManager.mysqlPassword);
        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
        try{
            this.connection = this.hikariDataSource.getConnection();
            Logger.info("Polaczenie z baza danych zostalo nawiazane!");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void openConnection(){
        try {
            closeConnection();
            this.connection = this.hikariDataSource.getConnection();
            Logger.info("Polaczenie z baza danych zostalo nawiazane!");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            if(!this.connection.isClosed()){
                this.connection.close();
            }
            if(this.connection != null){
                this.connection = null;
            }
            Logger.info("Polaczenie z mySQL zostalo zamkniete!");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() throws SQLException {
        if(this.connection == null) return false;
        if(this.connection.isClosed()) return false;
        return true;
    }

    public ResultSet query(String string) throws SQLException {
        if(!isConnected()){
            openConnection();
        }
        return this.connection.prepareStatement(string).executeQuery();
    }

    public int update(String string) throws SQLException {
        if(!isConnected()){
            openConnection();
        }
        return this.connection.prepareStatement(string).executeUpdate();
    }

    public PreparedStatement generateStatement(String string) throws SQLException {
        if(!isConnected()){
            openConnection();
        }
        return this.connection.prepareStatement(string);
    }
}
