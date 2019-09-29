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
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.NumberUtil;

public class GiveCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GiveCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(args.length < 2){
        return ChatUtil.sendMessage(sender, MessagesManager.usage("/give <nick> <przedmiot[:short]> <ilosc>"));
    }
    Player target = Bukkit.getPlayerExact(args[0]);
    if(target == null){
        return ChatUtil.sendMessage(sender, Lang.playerOffline);
    }
    String[] info = args[1].split(":");
    Material material = ItemUtil.getMaterial(info[0]);
    Short data = Short.valueOf((short)0);
    int amount = 0;
    if(material == null){
        return ChatUtil.sendMessage(sender, MessagesManager.error("Podany material nie istnieje!"));
    }
    if(info.length > 1){
        data = Short.valueOf(info[1]);
    }
    ItemStack item = null;
    if(args.length == 2){
        item = new ItemStack(material, 1, data);
        amount = 1;
    }
    else if(args.length == 3){
        item = new ItemStack(material, NumberUtil.isInt(args[2]) ? Integer.parseInt(args[2]) : 1, data);
        amount = NumberUtil.isInt(args[2]) ? Integer.parseInt(args[2]) : 1;
    }
    if(item == null){
        ChatUtil.sendMessage(sender, MessagesManager.error("Wystapil blad podczas tworzenia przedmiotu!"));
    }
    ItemUtil.giveItems(target, item);
    if(!sender.equals(target)){
        ChatUtil.sendMessage(sender, "&8>> {n}Dales {c}"+material.name()+" &8[&7"+amount+"&8] {n}graczowi {c}"+target.getName());
    }
    return ChatUtil.sendMessage(target, "&8>> {n}Otrzymales {c}"+material.name()+" &8[&7"+amount+"&8] {n}od {c}"+sender.getName());
    }
}

