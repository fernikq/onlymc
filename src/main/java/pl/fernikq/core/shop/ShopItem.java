package pl.fernikq.core.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private String name;
    private ItemStack itemStack;
    private int amount;
    private int price;

    public ShopItem(ItemStack itemStack, int amount, int price) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
