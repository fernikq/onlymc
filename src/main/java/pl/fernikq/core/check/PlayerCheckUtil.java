package pl.fernikq.core.check;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerCheckUtil {

    public static Set<Player> playerSet = new HashSet<>();

    public static Set<Player> getPlayerSet() {
        return playerSet;
    }
}
