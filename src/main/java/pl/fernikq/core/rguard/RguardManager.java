package pl.fernikq.core.rguard;

import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;

import java.util.*;

public class RguardManager {

    private final CorePlugin plugin;
    private Set<UUID> players;

    public RguardManager(CorePlugin plugin){
        this.plugin = plugin;
        this.players = new HashSet();
    }

    public boolean hasRguard(Player player){
        return this.players.contains(player.getUniqueId());
    }

    public void addToRguardPlayers(Player player){
        this.players.add(player.getUniqueId());
    }

    public void removeFromRguardPlayers(Player player){
        this.players.remove(player.getUniqueId());
    }

    public Set getPlayers() {
        return players;
    }
}
