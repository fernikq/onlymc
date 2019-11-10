package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.ShopActionType;
import pl.fernikq.core.shop.Shop;
import pl.fernikq.core.shop.ShopItem;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.StringUtil;

public class ShopAction implements InventoryAction {

    private final CorePlugin plugin;
    private final Shop shop;
    private final ShopItem shopItem;
    private final ShopActionType shopActionType;
    private final User user;

    public ShopAction(CorePlugin plugin, Shop shop, ShopActionType type, User user){
        this.plugin = plugin;
        this.shop = shop;
        this.shopItem = null;
        this.shopActionType = type;
        this.user = user;
    }

    public ShopAction(CorePlugin plugin, ShopItem shopItem, Shop shop, ShopActionType type, User user){
        this.plugin = plugin;
        this.shopItem = shopItem;
        this.shop = shop;
        this.shopActionType = type;
        this.user = user;
    }

    public ShopAction(CorePlugin plugin, ShopActionType type, User user){
        this.plugin = plugin;
        this.shop = null;
        this.shopItem = null;
        this.shopActionType = type;
        this.user = user;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(shopActionType.equals(ShopActionType.CHOOSE_BUY)){
            this.plugin.getUserInventory().shopBuyMenu(user).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.CHOOSE_SELL)){
            this.plugin.getUserInventory().shopSellMenu(user).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.CHOOSE_BUY_TYPE)){
            this.plugin.getUserInventory().shopBuy(user, shop).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.CHOOSE_SELL_TYPE)){
            this.plugin.getUserInventory().shopSell(user, shop).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.BACK_MENU)){
            this.plugin.getUserInventory().shopMenu(user).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.BACK_BUY_MENU)){
            this.plugin.getUserInventory().shopBuyMenu(user).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.BACK_SELL_MENU)){
            this.plugin.getUserInventory().shopSellMenu(user).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.BUY_ITEM)) {
            if(user.getUserStat().getCoins() < shopItem.getPrice()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
                return;
            }
            ItemUtil.giveItems(player, new ItemBuilder(shopItem.getItemStack().clone()).setAmount(shopItem.getAmount()).toItemStack());
            ChatUtil.sendMessage(player, StringUtil.replace(MessagesManager.shopBuyItem, "{AMOUNT}", shopItem.getPrice()));
            user.getUserStat().removeCoins(shopItem.getPrice());
            this.plugin.getUserInventory().shopBuy(user, shop).openInventory(player);
            return;
        }
        if(shopActionType.equals(ShopActionType.SELL_ITEM)) {
            int amount = ItemUtil.getAmountOfItem(player.getInventory(), shopItem.getItemStack().clone());
            if(amount < shopItem.getAmount()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz odpowiedniej ilosci tego przedmiotu!"));
                return;
            }
            ItemUtil.remove(shopItem.getItemStack().clone(), player, shopItem.getAmount());
            ChatUtil.sendMessage(player, StringUtil.replace(MessagesManager.shopSellItem, "{AMOUNT}", shopItem.getPrice()));
            user.getUserStat().addCoins(shopItem.getPrice());
            this.plugin.getUserInventory().shopSell(user, shop).openInventory(player);
            return;
        }
    }
}
