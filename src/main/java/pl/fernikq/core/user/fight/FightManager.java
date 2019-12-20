package pl.fernikq.core.user.fight;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

import java.util.HashSet;
import java.util.Set;

public class FightManager {

    private final CorePlugin plugin;
    private Set<User> usersDuringFight;

    public FightManager(CorePlugin plugin) {
        this.plugin = plugin;
        this.usersDuringFight = new HashSet<>();
    }

    public void removeFight(User user){
        this.usersDuringFight.remove(user);
        user.getUserFight().removeFight();
    }

    public Set<User> getUsersDuringFight() {
        return usersDuringFight;
    }
}
