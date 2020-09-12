package pl.fernikq.core.inventory.actions.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.shop.ShopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SellAllAction implements InventoryAction {

    private User user;
    private CorePlugin plugin;

    private Cache<User, Boolean> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    public SellAllAction(CorePlugin plugin, User user){
        this.user = user;
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(!this.cache.asMap().containsKey(this.user)){
            ChatUtil.sendMessage(player, "&8[&eSKLEP&8] &fAby sprzedac wszystkie przedmioty musisz potwiedzic swoja decyzje poprzez ponowne wcisniecie LPM.");
            this.cache.put(this.user, true);
            return;
        }
        this.cache.asMap().remove(this.user);
        AtomicBoolean sold = new AtomicBoolean(false);
        AtomicInteger coins = new AtomicInteger();
        this.plugin.getShopManager().getShops(ShopType.SELL).forEach(shop -> {
            shop.getItems().forEach(shopItem -> {
                int amount = ItemUtil.getAmountOfItem(player.getInventory(), shopItem.getItemStack().clone());
                if(amount >= shopItem.getAmount()) {
                    ItemUtil.remove(shopItem.getItemStack().clone(), player, shopItem.getAmount() * (amount / shopItem.getAmount()));
                    int price = shopItem.getPrice() * (amount / shopItem.getAmount());
                    user.getUserStat().addCoins(price);
                    coins.addAndGet(price);
                    sold.set(true);
                }
            });
        });
        if(sold.get()){
            ChatUtil.sendMessage(player, "&8[&eSKLEP&8] &fSprzedales wszystkie mozliwe przedmioty za &e"+coins.get()+" &fmonet!");
            user.getSidebar().update();
        }else{
            ChatUtil.sendMessage(player, "&8[&eSKLEP&8] &fNie posiadasz zadnego przedmiotu ktory nadaje sie do sprzedania :(");
        }
    }
}
