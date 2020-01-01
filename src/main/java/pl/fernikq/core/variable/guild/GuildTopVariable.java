package pl.fernikq.core.variable.guild;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;

public class GuildTopVariable extends Variable {

    private CorePlugin plugin;
    private int position;

    public GuildTopVariable(String string, CorePlugin plugin, int position){
        super(string);
        this.plugin = plugin;
        this.position = position;
    }

    @Override
    public String getReplacement(Player player) {
        Guild guild = (Guild) this.plugin.getTopManager().getTopByType(TopType.GUILD_POINTS).getObjectByPosition(this.position);
        if(guild != null){
            return MessagesManager.tablistGuildrTopFormat.replace("{GUILD-TAG}", guild.getTag()).replace("{GUILD-POINTS}", Integer.toString(guild.getPoints()));
        }
        return "Brak";
    }
}
