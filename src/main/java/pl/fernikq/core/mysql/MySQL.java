package pl.fernikq.core.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.util.Logger;

import java.sql.*;

public class MySQL {

    private final CorePlugin plugin;
    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public MySQL(CorePlugin plugin){
        this.plugin = plugin;
        this.hikariConfig = new HikariConfig();
        this.hikariConfig.setUsername(ConfigManager.mysqlUser);
        this.hikariConfig.setPassword(ConfigManager.mysqlPassword);
        this.hikariConfig.setJdbcUrl("jdbc:mysql://"+ConfigManager.mysqlHost+":"+ConfigManager.mysqlPort+"/"+ConfigManager.mysqlBase+"?useSSL=false");
        this.hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        this.hikariConfig.setPoolName("Core-MySQL");
        this.hikariConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        this.hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
    }

    public void close(){
        if(isRunning()){
            this.hikariDataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    public boolean isRunning() {
        return this.hikariDataSource.isRunning();
    }
}
