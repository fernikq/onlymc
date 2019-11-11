package pl.fernikq.core.mysql;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.util.Logger;

import java.sql.*;

public class MySQL {

    private final CorePlugin plugin;
    private Connection connection;
    private BukkitTask connectionTask;

    public MySQL(CorePlugin plugin){
        this.plugin = plugin;
        openConnection();
        closeConnection();
    }

    public void openConnection(){
        try {
            if(isConnected()){
                if(this.connectionTask != null){
                    this.connectionTask.cancel();
                }
                this.connectionTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    closeConnection();
                }, 3600);
                return;
            }
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.connection = DriverManager.getConnection("jdbc:mysql://" + ConfigManager.mysqlHost + ":" + ConfigManager.mysqlPort + "/" + ConfigManager.mysqlBase, ConfigManager.mysqlUser, ConfigManager.mysqlPassword);
            this.connectionTask = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                closeConnection();
            }, 3600);
        }catch(SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex){
            ex.printStackTrace();
        }
    }

    public void openWithoutTask(){
        try{
            if(isConnected()){
                return;
            }
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.connection = DriverManager.getConnection("jdbc:mysql://" + ConfigManager.mysqlHost + ":" + ConfigManager.mysqlPort + "/" + ConfigManager.mysqlBase, ConfigManager.mysqlUser, ConfigManager.mysqlPassword);
        }catch(SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex){
            ex.printStackTrace();
        }
    }


    public void closeConnection(){
        try {
            if(this.connection == null){
                return;
            }
            if(!this.connection.isClosed()){
                this.connection.close();
            }
            this.connection = null;
            if(this.connectionTask != null){
                this.connectionTask.cancel();
                this.connectionTask = null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet query(String string) throws SQLException {
        return this.connection.prepareStatement(string).executeQuery();
    }

    public int update(String string) throws SQLException {
        return this.connection.prepareStatement(string).executeUpdate();
    }

    public PreparedStatement generateStatement(String string) throws SQLException {
        return this.connection.prepareStatement(string);
    }
}
