package pl.fernikq.core.magiccase;

import org.bukkit.inventory.ItemStack;

public class MagicCaseDrop {

    private String name;
    private ItemStack itemStack;
    private int minAmount;
    private int maxAmount;

    public void setName(String name) {
        this.name = name;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}
