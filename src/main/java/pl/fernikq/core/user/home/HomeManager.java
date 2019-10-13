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
        return user.getHomes().containsKey(name.toLowerCase());
    }

    public Home get(User user, String name){
        return user.getHomes().get(name.toLowerCase());
    }

    public Home get(User user, int index){
        return user.getHomeList().get(index);
    }

    public String getHomesToString(User user){
        StringBuilder sb = new StringBuilder();
        for(Home home : user.getHomes().values()){
            sb.append("&8, {c}").append(home.getName());
        }
        return sb.toString().replaceFirst("&8, ", "");
    }

    public void create(User user, String name, Location location){
        Home home = new Home(user, name, location);
        user.addHome(home);
        this.homeData.insertHome(home);
    }

    public void delete(User user, Home home){
        user.removeHome(home);
        this.homeData.deleteHome(home);
    }

    public void init(){
        this.homeData = new HomeData(this.plugin);
    }

    public HomeData getHomeData() {
        return homeData;
    }
}
