package pl.fernikq.core.command.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.EnchantManager;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.NumberUtil;

public class EnchantCommand extends CustomCommand {

    private final CorePlugin plugin;

    public EnchantCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 2) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/enchant <zaklecie> <poziom>"));
        }
        Player player = (Player)sender;
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            return ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz enchantowac tego przedmiotu!"));
        }
        Enchantment enchant = EnchantManager.get(args[0]);
        if(enchant == null) {
            return ChatUtil.sendMessage(player, MessagesManager.error("Podany enchant nie istnieje!"));
        }
        if(!NumberUtil.isInt(args[1])) {
            return ChatUtil.sendMessage(player, Lang.badIntegerFormat);
        }
        new ItemBuilder(player.getItemInHand()).addEnchant(enchant, Integer.parseInt(args[1]));
        return ChatUtil.sendMessage(player, "&8>> {n}Przedmiot {c}"+player.getItemInHand().getType().name()+" {n}zostal zaklety!");
    }
}
