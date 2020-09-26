package pl.fernikq.core.command.admin.magicCase;

import org.bukkit.Bukkit;
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
import pl.fernikq.core.util.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class CaseCreateCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CaseCreateCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/case create <normal, premium>"));
        }
        Player player = (Player)sender;
        Block block = BlockUtil.getTargetBlock(player, 5, Arrays.asList(Material.ENDER_CHEST, Material.CHEST));
        if(Objects.isNull(block)){
            return ChatUtil.sendMessage(player, MessagesManager.error("Musisz patrzec sie na skrzynie!"));
        }
        if(this.plugin.getMagicCaseManager().isMagicCaseAtLocation(block.getLocation())){
            return ChatUtil.sendMessage(player, MessagesManager.error("W tym miejscu znajduje sie juz magiczna skrzynia!"));
        }
        MagicCaseType magicCaseType = MagicCaseType.getMagicCaseTypeByName(args[1]);
        if(magicCaseType == null){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/casecreate <normal, premium>"));
        }
        this.plugin.getMagicCaseManager().addCase(block.getLocation(), new MagicCase(magicCaseType));
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getMagicCaseManager().getMagicCaseData().saveMagicCase(block.getLocation(), magicCaseType));
        return ChatUtil.sendMessage(player, "&8>> &aPomsylnie utworzyles skrzynie typu: "+magicCaseType);
    }
}
