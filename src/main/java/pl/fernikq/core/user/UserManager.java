package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserManager {

    private final CorePlugin plugin;
    private ConcurrentMap<UUID, User> users;
    private UserData userData;

    public UserManager(CorePlugin plugin){
        this.plugin = plugin;
        this.users = new ConcurrentHashMap<>();
    }

    public void init(){
        this.userData = new UserData(this.plugin);
    }

    public boolean isCorrect(String name){
        User user = getUser(name).getOrNull();
        if(user == null){
            return true;
        }
        return user.getName().equals(name);
    }

    public User getUser(Player player){
        return this.users.computeIfAbsent(player.getUniqueId(), uuid -> {
           User user = new User(player);
           this.userData.insertUser(user);
           return user;
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
        this.users.remove(user.getUuid());
        this.userData.deleteUser(user);
    }

    public Set<User> getUsers(){
        return HashSet.ofAll(new ConcurrentHashMap<UUID, User>(this.users).values());
    }

    public UserData getUserData() {
        return userData;
    }
}
