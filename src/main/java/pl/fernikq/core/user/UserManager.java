package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.user.backup.BackupData;
import pl.fernikq.core.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserManager {

    private final CorePlugin plugin;
    private ConcurrentMap<UUID, User> usersByUUID;
    private ConcurrentMap<String, User> usersByName;
    private UserData userData;
    private UserStatData userStatData;
    private BackupData backupData;

    public UserManager(CorePlugin plugin){
        this.plugin = plugin;
        this.usersByUUID = new ConcurrentHashMap<>();
        this.usersByName = new ConcurrentHashMap<>();
    }

    public void init(){
        this.userData = new UserData(this.plugin);
        this.userStatData = new UserStatData(this.plugin);
        this.backupData = new BackupData(this.plugin);
    }

    public boolean isCorrect(String name){
        User user = getUser(name).getOrNull();
        if(user == null){
            return true;
        }
        return user.getName().equals(name);
    }

    public User getUser(Player player){
        if(this.usersByUUID.containsKey(player.getUniqueId())){
            User user = this.usersByUUID.get(player.getUniqueId());
            if(!user.getName().equals(player.getName())){
                this.usersByName.remove(user.getName().toLowerCase());
                user.setName(player.getName());
                this.usersByName.put(player.getName().toLowerCase(), user);
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    this.updateUserInfo(user);
                });
            }
            return user;
        }
        Option<User> userByName = getUser(player.getName());
        if(userByName.isDefined()){
            User user = userByName.get();
            this.usersByUUID.remove(user.getUuid());
            UUID oldUUID = user.getUuid();
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                updateUUID(user, oldUUID, player.getUniqueId());
            });
            user.setUuid(player.getUniqueId());
            this.usersByUUID.put(player.getUniqueId(), user);
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.updateUserInfo(user);
            });
            return user;
        }
        User user = new User(player);
        this.usersByUUID.put(player.getUniqueId(), user);
        this.usersByName.put(player.getName().toLowerCase(), user);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.insertUser(user);
        });
        this.plugin.getTopManager().getTopsByKind(TopKind.USER).forEach(sortable -> {
            sortable.addObject(user);
            sortable.sort();
        });
        PlayerUtil.randomTeleport(player, true);
        return user;
    }

    public List<String> getUsersNames(List<User> userList){
        List<String> names = new ArrayList<>();
        userList.forEach(user -> names.add(user.getName()));
        return names;
    }

    public java.util.Set<User> getOnlineUsers(){
        java.util.Set<User> online = new java.util.HashSet<>();
        Bukkit.getOnlinePlayers().forEach(o -> {
            User user = getUser(o.getUniqueId()).getOrNull();
            if(user != null){
                online.add(user);
            }
        });
        return online;
    }

    public void insertUser(User user){
        this.plugin.runAsync(() -> {
            this.userData.insertUser(user);
            this.userStatData.insertStats(user);
        });
    }

    public void updateUser(User user){
        this.userData.updateUser(user);
        this.userStatData.updateStats(user);
    }

    public void updateUserInfo(User user){
        this.plugin.runAsync(() -> this.userData.updateUser(user));
    }

    private void removeUser(User user){
        this.plugin.runAsync(() -> {
            this.userData.deleteUser(user);
            this.userStatData.deleteUser(user);
            this.plugin.getTopManager().getTopsByKind(TopKind.USER).forEach(sortable -> {
                sortable.removeObject(user);
                sortable.sort();
            });
        });
    }

    public void updateUUID(User user, UUID oldUUID, UUID newUUID){
        this.plugin.runAsync(() -> {
            this.userStatData.updateUUID(oldUUID, newUUID);
            this.plugin.getHomeManager().getHomeData().updateUUID(oldUUID, newUUID);
            this.backupData.updateUUID(oldUUID, newUUID);
            if(user.hasGuild()){
                this.plugin.getGuildManager().updateUUID(oldUUID, newUUID);
            }
        });
    }

    public Option<User> getUser(String name){
        return Option.of(this.usersByName.get(name.toLowerCase()));
    }

    public Option<User> getUser(UUID uuid){
        return Option.of(this.usersByUUID.get(uuid));
    }

    public void registerUser(User user){
        this.usersByUUID.putIfAbsent(user.getUuid(), user);
        this.usersByName.put(user.getName().toLowerCase(), user);
    }

    public void deleteUser(User user){
        removeUser(user);
        this.usersByUUID.remove(user.getUuid());
        this.usersByName.remove(user.getName().toLowerCase());
    }

    public Set<User> getUsers(){
        return HashSet.ofAll(new ConcurrentHashMap<UUID, User>(this.usersByUUID).values());
    }

    public BackupData getBackupData() {
        return backupData;
    }

    public UserData getUserData() {
        return userData;
    }
}
