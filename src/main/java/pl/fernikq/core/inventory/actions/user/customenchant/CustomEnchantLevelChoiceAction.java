package pl.fernikq.core.inventory.actions.user.customenchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.customenchant.CustomEnchantItemEnum;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.util.Objects;

public class CustomEnchantLevelChoiceAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final ItemStack itemStack;
    private final Enchantment enchantment;
    private final int level;
    private final int cost;
    private final CustomEnchantItemEnum customEnchantItemEnum;
    private final boolean back;

    public CustomEnchantLevelChoiceAction(CorePlugin plugin, User user, ItemStack itemStack, Enchantment enchantment, int level, int cost, CustomEnchantItemEnum customEnchantItemEnum, boolean back){
        this.plugin = plugin;
        this.user = user;
        this.itemStack = itemStack;
        this.enchantment = enchantment;
        this.level = level;
        this.cost = cost;
        this.customEnchantItemEnum = customEnchantItemEnum;
        this.back = back;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(this.back){
            this.plugin.getUserInventory().customEnchantMenu(this.user, this.itemStack, this.customEnchantItemEnum).openInventory(player);
            return;
        }
        if(this.cost > player.getLevel()){
            ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wymaganego poziomu!"));
            return;
        }
        player.setLevel(player.getLevel() - this.cost);
        new ItemBuilder(player.getItemInHand()).addEnchant(this.enchantment, this.level);
        this.plugin.getUserInventory().customEnchantMenu(this.user, this.itemStack, this.customEnchantItemEnum).openInventory(player);
        return;
    }
}
