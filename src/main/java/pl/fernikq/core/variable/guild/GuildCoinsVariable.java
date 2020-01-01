package pl.fernikq.core.variable.guild;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class GuildCoinsVariable extends Variable {

    private CorePlugin plugin;

    public GuildCoinsVariable(String string, CorePlugin plugin){
        super(string);
        this.plugin = plugin;
    }

    @Override
    public String getReplacement(Player player) {
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(user != null && user.hasGuild()){
            return Integer.toString(user.getGuild().getTreasure().getCoins());
        }
        return "Brak";
    }
}
