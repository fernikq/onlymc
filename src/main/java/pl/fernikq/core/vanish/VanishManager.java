package pl.fernikq.core.vanish;

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.UserGroup;

import java.util.ArrayList;
import java.util.List;

public class VanishManager {

    private List<Player> vanished;
    private final CorePlugin plugin;

    public VanishManager(CorePlugin plugin){
        this.plugin = plugin;
        this.vanished = new ArrayList<>();
    }

    public void addVanished(Player player){
        if(vanished.contains(player)){
            return;
        }
        vanished.add(player);
    }

    public void removeVanished(Player player){
        if(!vanished.contains(player)){
            return;
        }
        vanished.remove(player);
    }

    public void hide(Player player){
        Bukkit.getOnlinePlayers().forEach(online -> {
            this.plugin.getUserManager().getUser(online.getUniqueId()).filter(user -> !user.canByGroup(UserGroup.HELPER)).peek(user -> {
               online.hidePlayer(player);
            });
        });
    }

    public void show(Player player){
        Bukkit.getOnlinePlayers().forEach(online -> online.showPlayer(player));
    }

    public boolean isVanished(Player player){
        return this.vanished.contains(player);
    }

    public List<Player> getVanished() {
        return new ArrayList<>(this.vanished);
    }
}
