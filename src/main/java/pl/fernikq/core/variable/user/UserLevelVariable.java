package pl.fernikq.core.variable.user;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class UserLevelVariable extends Variable {

    private CorePlugin plugin;

    public UserLevelVariable(String string, CorePlugin plugin){
        super(string);
        this.plugin = plugin;
    }

    @Override
    public String getReplacement(Player player) {
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null){
            return Integer.toString(user.getUserStat().getLevel());
        }
        return "";
    }
}
