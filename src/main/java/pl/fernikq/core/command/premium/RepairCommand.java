package pl.fernikq.core.command.premium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.EnchantManager;

public class RepairCommand extends CustomCommand {

    private final CorePlugin plugin;

    public RepairCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length == 0){
            if(!EnchantManager.canRepair(player.getItemInHand())){
                return ChatUtil.sendMessage(sender, MessagesManager.error("Podany przedmiot nie moze zostac naprawiony!"));
            }
            player.getItemInHand().setDurability((short)0);
            return ChatUtil.sendMessage(sender, "&8>> &aPomyslnie {n}naprawiles przedmiot&8!");
        }
        if(!args[0].equalsIgnoreCase("all")){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/repair | /repair all"));
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(!user.canByGroup(UserGroup.MVP)){
               ChatUtil.sendMessage(sender, "&8>> {n}Aby uzyc tej komendy musisz posiadac range "+UserGroup.MVP.getPrefix());
               return;
           }
           for(ItemStack itemStack : player.getInventory().getContents()){
               if(EnchantManager.canRepair(itemStack)){
                   itemStack.setDurability((short)0);
               }
           }
           ChatUtil.sendMessage(sender, "&8>> &aPomyslnie {n}naprawiles wszystkie przedmioty&8!");
           return;
        });
        return true;
    }
}
