package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.user.backup.BackupData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserManager {

    private final CorePlugin plugin;
    private ConcurrentMap<UUID, User> users;
    private UserData userData;
    private UserStatData userStatData;
    private BackupData backupData;

    public UserManager(CorePlugin plugin){
        this.plugin = plugin;
        this.users = new ConcurrentHashMap<>();
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
        if(this.users.containsKey(player.getUniqueId())){
            return this.users.get(player.getUniqueId());
        }
        User userByName = getUser(player.getName()).getOrNull();
        if(userByName != null){
            this.users.remove(userByName.getUuid());
            userByName.setUuid(player.getUniqueId());
            this.users.putIfAbsent(userByName.getUuid(), userByName);
            updateUserInfo(userByName);
            return userByName;
        }
        return this.users.computeIfAbsent(player.getUniqueId(), uuid -> {
            User user = new User(player);
            insertUser(user);
            this.plugin.getTopManager().getTopsByKind(TopKind.USER).forEach(sortable -> {
                sortable.addObject(user);
                sortable.sort();
            });
            return user;
        });
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

    public Option<User> getUser(String name){
        return getUsers().find(user -> user.getName().equalsIgnoreCase(name));
    }

    public Option<User> getUser(UUID uuid){
        return Option.of(this.users.get(uuid));
    }

    public void registerUser(User user){
        this.users.putIfAbsent(user.getUuid(), user);
    }

    public void deleteUser(User user){
        removeUser(user);
        this.users.remove(user.getUuid());
    }

    public Set<User> getUsers(){
        return HashSet.ofAll(new ConcurrentHashMap<UUID, User>(this.users).values());
    }

    public BackupData getBackupData() {
        return backupData;
    }
}
