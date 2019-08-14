package pl.fernikq.core.user.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class HomeManager {

    private final CorePlugin plugin;
    private HomeData homeData;

    public HomeManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public boolean canCreate(User user){
        return user.getHomes().size() < user.getGroup().getHomes();
    }

    public boolean exists(User user, String name){
        return user.getHomes().containsKey(name);
    }

    public Home get(User user, String name){
        return user.getHomes().get(name);
    }

    public void create(User user, String name, Location location){
        Home home = new Home(user, name, location);
        user.getHomes().put(home.getName() ,home);
        this.homeData.insertHome(home);
    }

    public void delete(User user, Home home){
        user.getHomes().remove(home);
        this.homeData.deleteHome(home);
    }

    public void init(){
        this.homeData = new HomeData(this.plugin);
    }

    public HomeData getHomeData() {
        return homeData;
    }
}
