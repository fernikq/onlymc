package pl.fernikq.core.variable.user;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;

public class UserTopVariable extends Variable {

    private CorePlugin plugin;
    private int position;

    public UserTopVariable(String string, CorePlugin plugin, int position){
        super(string);
        this.plugin = plugin;
        this.position = position;
    }

    @Override
    public String getReplacement(Player player) {
        User user = (User) this.plugin.getTopManager().getTopByType(TopType.USER_POINTS).getObjectByPosition(this.position);
        if(user != null){
            return MessagesManager.tablistUserTopFormat.replace("{USER-NAME}", user.getName()).replace("{USER-POINTS}", Integer.toString(user.getUserStat().getPoints()));
        }
        return "Brak";
    }
}
