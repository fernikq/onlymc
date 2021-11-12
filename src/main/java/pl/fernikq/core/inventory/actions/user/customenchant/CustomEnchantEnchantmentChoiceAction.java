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

import java.util.Objects;

public class CustomEnchantEnchantmentChoiceAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final ItemStack itemStack;
    private final Enchantment enchantment;
    private final CustomEnchantItemEnum customEnchantItemEnum;

    public CustomEnchantEnchantmentChoiceAction(CorePlugin plugin, User user, ItemStack itemStack, Enchantment enchantment, CustomEnchantItemEnum customEnchantItemEnum){
        this.plugin = plugin;
        this.user = user;
        this.itemStack = itemStack;
        this.enchantment = enchantment;
        this.customEnchantItemEnum = customEnchantItemEnum;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        Integer maxLevelRestrict = ConfigManager.getEnchantmentIntegerMap().get(this.enchantment);
        if(Objects.nonNull(maxLevelRestrict) && maxLevelRestrict.intValue() <= 0){
            ChatUtil.sendMessage(player, MessagesManager.error("Ten enchant zostal wylaczony!"));
            return;
        }
        this.plugin.getUserInventory().customEnchantLevelChoice(this.user, this.itemStack, this.enchantment, this.customEnchantItemEnum).openInventory(player);
        return;
    }
}
