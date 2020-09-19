package pl.fernikq.core.command.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;

public class AdminBossCommand extends CustomCommand {

    private final CorePlugin plugin;

    private ItemStack[] bossArmor = new ItemStack[]{new ItemBuilder(Material.DIAMOND_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.DURABILITY, 3).toItemStack(),
            new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).addEnchant(Enchantment.DURABILITY, 3).toItemStack()};

    public AdminBossCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        if(args.length < 1) {
            return ChatUtil.sendMessage(player, MessagesManager.usage("/aboss <respawn>"));
        }
        if(args[0].equalsIgnoreCase("spawn")){
            Giant giant = (Giant) player.getWorld().spawnEntity(player.getLocation(), EntityType.GIANT);
            giant.setCustomName(ChatUtil.fixColor(this.plugin.getBossManager().getGiantBossName()));
            giant.setCustomNameVisible(true);
            giant.setCanPickupItems(false);
            giant.setMaxHealth(this.plugin.getBossManager().getGiantBossHealth());
            giant.setHealth(this.plugin.getBossManager().getGiantBossHealth());
            giant.getEquipment().setArmorContents(this.bossArmor);
            this.plugin.getServer().getOnlinePlayers().forEach(online -> {
                ChatUtil.sendMessage(online, "&8[&eBOSSY&8] &fNa swiecie pojawila sie bestia! Jej miejsce pobytu: &eX&8: &f"+player.getLocation().getBlockX()+" &eZ&8: &f"+player.getLocation().getBlockZ());
                ChatUtil.sendMessage(online, "&8[&eBOSSY&8] &fUwolnij swiat od zla! Bestia ma przy sobie &6cenne &fprzedmioty, ktore mozesz od niej pozyskac, wiec nie trac czasu i ruszaj!");
            });
            return ChatUtil.sendMessage(sender, "&8>> &fPomyslnie utworzyles bossa");
        }
        if(args[0].equalsIgnoreCase("reload")){
            this.plugin.getBossManager().reload();
            return ChatUtil.sendMessage(player, "&8>> &fPomsylnie przeladowales konfiguracje bossow!");
        }
        return true;
    }
}
