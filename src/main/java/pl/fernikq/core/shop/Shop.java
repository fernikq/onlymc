package pl.fernikq.core.shop;

import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.kit.KitItem;

import java.util.List;

public class Shop {

    private ItemStack item;
    private ShopType shopType;
    private List<ShopItem> items;

    public Shop(ItemStack item, ShopType shopType) {
        this.item = item;
        this.shopType = shopType;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public ShopType getShopType() {
        return shopType;
    }
}
