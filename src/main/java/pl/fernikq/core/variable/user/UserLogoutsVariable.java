package pl.fernikq.core.variable.user;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class UserLogoutsVariable extends Variable {

    private CorePlugin plugin;

    public UserLogoutsVariable(String string, CorePlugin plugin){
        super(string);
        this.plugin = plugin;
    }

    @Override
    public String getReplacement(Player player) {
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null){
            return Integer.toString(user.getUserStat().getLogouts());
        }
        return "";
    }
}
