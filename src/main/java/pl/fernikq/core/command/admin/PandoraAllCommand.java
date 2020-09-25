package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.NumberUtil;

public class PandoraAllCommand extends CustomCommand {

    private final CorePlugin plugin;

    public PandoraAllCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/pandoraall <ilosc>"));
        }
        if(!NumberUtil.isInt(args[0])){
            return ChatUtil.sendMessage(sender, Lang.badIntegerFormat);
        }
        int amount = Integer.parseInt(args[0]);
        if(amount < 1){
            return ChatUtil.sendMessage(sender, Lang.integerLessThanOne);
        }
        Bukkit.getOnlinePlayers().forEach(online -> {
            ItemUtil.giveItems(online, new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone()).setAmount(amount).toItemStack());
            ChatUtil.sendMessage(online, "&8>> {n}Kazdy na serwerze otrzymal "+this.plugin.getDropManager().getPremiumCaseItem().getItemMeta().getDisplayName()+" {n}w ilosci {c}"+amount);
        });
        return true;
    }
}
