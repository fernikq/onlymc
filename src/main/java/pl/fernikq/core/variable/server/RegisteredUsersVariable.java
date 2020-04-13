package pl.fernikq.core.variable.server;

import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

public class RegisteredUsersVariable extends Variable {

    private CorePlugin plugin;

    public RegisteredUsersVariable(String string, CorePlugin plugin){
        super(string);
        this.plugin = plugin;
    }

    @Override
    public String getReplacement(Player player) {
        return Integer.toString(this.plugin.getUserManager().getUsers().length());
    }
}
