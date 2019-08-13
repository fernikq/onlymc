package pl.fernikq.core;

import org.bukkit.plugin.java.JavaPlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.mysql.MySQL;

public class CorePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MySQL mySQL;

    @Override
    public void onEnable() {
        initConfigurations();
        initDatabase();
    }

    @Override
    public void onDisable(){

    }

    private void initDatabase(){
        this.mySQL = new MySQL(this);
    }

    private void initConfigurations(){
        this.configManager = new ConfigManager(this);

        this.configManager.reload();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MySQL getMySQL() {
        return mySQL;
    }
}
