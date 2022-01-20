package pl.fernikq.core.command.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.shop.ShopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SellAllCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SellAllCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
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
        return true;
    }
}
