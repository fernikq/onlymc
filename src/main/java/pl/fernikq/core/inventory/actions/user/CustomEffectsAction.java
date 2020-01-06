package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class CustomEffectsAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final int cost;
    private final PotionEffect potionEffect;
    private final String potionEffectName;

    public CustomEffectsAction(CorePlugin plugin, PotionEffect potionEffect, int cost, String potionEffectName, User user){
        this.plugin = plugin;
        this.potionEffect = potionEffect;
        this.cost = cost;
        this.user = user;
        this.potionEffectName = potionEffectName;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(user.getUserStat().getCoins() < this.cost){
            ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
            return;
        }
        if(player.hasPotionEffect(this.potionEffect.getType())){
            player.removePotionEffect(this.potionEffect.getType());
        }
        player.addPotionEffect(this.potionEffect);
        user.getUserStat().removeCoins(this.cost);
        ChatUtil.sendMessage(player, "&8>> {n}Pomyslnie kupiles efekt {c}"+this.potionEffectName+" {n}za {c}"+this.cost+" {n}monet!");
        user.getSidebar().update();
    }
}
