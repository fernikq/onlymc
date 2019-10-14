package pl.fernikq.core.command.admin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.NumberUtil;

import java.util.List;

public class LoreCommand extends CustomCommand {

    private final CorePlugin plugin;

    public LoreCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/lore <add> <lore> || /lore <remove> <numer>"));
        }
        String lore = StringUtils.join(args, " ", 1, args.length);
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Podany przedmiot nie moze zostac nazwany!"));
        }
        switch(args[0].toLowerCase()){
            case "add":{
                if(args.length < 2){
                    return ChatUtil.sendMessage(sender, MessagesManager.usage("/lore <add> <lore>"));
                }
                new ItemBuilder(player.getItemInHand()).addLore(ChatUtil.fixColor(lore));
                return ChatUtil.sendMessage(sender, "&8>> &aPomyslnie {n}dodales lore do przedmiotu!");
            }
            case "remove":{
                if(args.length != 2) {
                    return ChatUtil.sendMessage(sender, MessagesManager.usage("/lore <remove> <numer>"));
                }
                if(!player.getItemInHand().getItemMeta().hasLore()){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Podany przedmiot nie ma lore!"));
                }
                if(!NumberUtil.isInt(args[1])){
                    return ChatUtil.sendMessage(sender, Lang.badIntegerFormat);
                }
                int number = Integer.parseInt(args[1]);
                if(number <= 0){
                    return ChatUtil.sendMessage(sender, Lang.integerLessThanOne);
                }
                if(player.getItemInHand().getItemMeta().getLore().size() < number){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Przedmiot nie posiada podanego lore'u!"));
                }
                List<String> newLore = player.getInventory().getItemInHand().getItemMeta().getLore();
                newLore.remove(number-1);
                new ItemBuilder(player.getItemInHand()).setLore(ChatUtil.fixColor(newLore));
                return ChatUtil.sendMessage(sender, "&8>> &aPomyslnie {n}usunales lore z przedmiotu!");
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/lore <add> <lore> || /lore <remove> <numer>"));
            }
        }
    }
}
