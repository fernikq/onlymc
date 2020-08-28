package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class MemoryCommand extends CustomCommand {

    private final CorePlugin plugin;
    private final Runtime runtime = Runtime.getRuntime();

    public MemoryCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int max = (int) (this.runtime.maxMemory() / 1024L / 1024L);
        int total = (int) (this.runtime.totalMemory() / 1024L / 1024L);
        int free = (int) (this.runtime.freeMemory() / 1024L / 1024L);
        int available = max - total;
        ChatUtil.sendMessage(sender, " ");
        ChatUtil.sendMessage(sender, "&8>> &fMaksymalny RAM&8: {c}"+max+" MB");
        ChatUtil.sendMessage(sender, "&8>> &fDostepny RAM&8: {c}"+available+" MB");
        ChatUtil.sendMessage(sender, "&8>> &fUzywany RAM&8: {c}"+total+" MB");
        ChatUtil.sendMessage(sender, "&8>> &fWolny RAM&8: {c}"+free+" MB");
        ChatUtil.sendMessage(sender, " ");
        return true;
    }
}
