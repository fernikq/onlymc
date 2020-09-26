package pl.fernikq.core.command.admin.magicCase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.magiccase.MagicCase;
import pl.fernikq.core.magiccase.MagicCaseType;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.BlockUtil;
import pl.fernikq.core.util.ChatUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class CaseRemoveCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CaseRemoveCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        Block block = BlockUtil.getTargetBlock(player, 5, Arrays.asList(Material.ENDER_CHEST, Material.CHEST));
        if(Objects.isNull(block)){
            return ChatUtil.sendMessage(player, MessagesManager.error("Musisz patrzec sie na skrzynie!"));
        }
        if(!this.plugin.getMagicCaseManager().isMagicCaseAtLocation(block.getLocation())){
            return ChatUtil.sendMessage(player, MessagesManager.error("W tym miejscu nie znajduje sie zadna magiczna skrzynia!"));
        }
        Location location = block.getLocation().clone();
        MagicCase magicCase = this.plugin.getMagicCaseManager().getMagicCaseMap().get(location);
        this.plugin.getMagicCaseManager().removeCase(block.getLocation());
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getMagicCaseManager().getMagicCaseData().deleteMagicCase(location, magicCase.getType()));
        return ChatUtil.sendMessage(player, "&8>> &cPomyslnie usunales magiczna skrzynie!");
    }
}
