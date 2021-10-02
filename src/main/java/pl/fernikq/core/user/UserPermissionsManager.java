package pl.fernikq.core.user;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionAttachment;
import pl.fernikq.core.CorePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserPermissionsManager {

    private final CorePlugin plugin;
    private File permissionFile;

    public UserPermissionsManager(CorePlugin plugin){
        this.plugin = plugin;
        checkFile();
        loadPermissions();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        permissionFile = new File(this.plugin.getDataFolder(), "permissions.yml");
        if(!permissionFile.exists()){
            this.plugin.saveResource("permissions.yml", true);
        }
    }

    public void reload(){
        checkFile();
        loadPermissions();
    }

    public void loadPermissions(){
        ConfigurationSection configurationSection = getPermissionsFile().getConfigurationSection("Permissions");
        for(String group : configurationSection.getKeys(false)){
            ConfigurationSection configuration = configurationSection.getConfigurationSection(group);
            UserGroup userGroup = UserGroup.getByName(group);
            if(userGroup == null){
                continue;
            }
            List<String> permissions = new ArrayList<>();
            permissions = configuration.getStringList("permissions");
            userGroup.setPermissions(permissions);
        }
    }

    public void reloadPermissions(User user){
        if(user.asPlayer() == null){
            return;
        }
        PermissionAttachment permissionAttachment = user.asPlayer().addAttachment(this.plugin);
        for(String permission : user.getGroup().getPermissions()){
            permissionAttachment.setPermission(permission, true);
        }
    }

    public void removePermissions(User user){
        if(user.asPlayer() == null){
            return;
        }
        PermissionAttachment permissionAttachment = user.asPlayer().addAttachment(this.plugin);
        for(String permission : user.getGroup().getPermissions()){
            permissionAttachment.setPermission(permission, false);
        }
    }

    public YamlConfiguration getPermissionsFile() {
        return YamlConfiguration.loadConfiguration(permissionFile);
    }
}
