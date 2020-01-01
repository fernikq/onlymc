package pl.fernikq.core.variable.guild;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class GuildLogoutsVariable extends Variable {

    private CorePlugin plugin;

    public GuildLogoutsVariable(String string, CorePlugin plugin){
        super(string);
        this.plugin = plugin;
    }

    @Override
    public String getReplacement(Player player) {
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null && user.hasGuild()){
            return Integer.toString(user.getGuild().getLogouts());
        }
        return "Brak";
    }
}
