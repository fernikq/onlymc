package pl.fernikq.core.protection;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

import java.util.*;

public class ProtectionManager {

    private final CorePlugin plugin;
    private final Map<UUID, ProtectedUser> protectedUsers = new HashMap<>();

    public ProtectionManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public void addUser(ProtectedUser user){
        this.protectedUsers.put(user.getUuid(), user);
    }

    public void removeUser(UUID uuid){
        this.protectedUsers.remove(uuid);
    }

    public boolean isProtected(UUID uuid){
        return this.protectedUsers.containsKey(uuid);
    }

    public Set<ProtectedUser> getProtectedUsers() {
        return new HashSet<>(this.protectedUsers.values());
    }
}
