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

    public UserManager(CorePlugin plugin){
        this.plugin = plugin;
        this.users = new ConcurrentHashMap<>();
    }

    public User getUser(Player player){
        return this.users.computeIfAbsent(player.getUniqueId(), uuid -> {
           User user = new User(player);
           //TODO insert user
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

    public Set<User> getUsers(){
        return HashSet.ofAll(this.users.values());
    }
}
