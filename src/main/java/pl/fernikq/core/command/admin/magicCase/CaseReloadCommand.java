package pl.fernikq.core.command.admin.magicCase;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.BlockUtil;
import pl.fernikq.core.util.ChatUtil;

import java.util.Arrays;
import java.util.Objects;

public class CaseReloadCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CaseReloadCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.getMagicCaseManager().load();
        return ChatUtil.sendMessage(sender, "&8>> &fPomyslnie przeladowales konfiguracje magicznych skrzyn");
    }
}
