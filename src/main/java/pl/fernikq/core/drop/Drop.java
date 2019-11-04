package pl.fernikq.core.drop;

import org.bukkit.inventory.ItemStack;

public class Drop {

    private String name;
    private DropType dropType;
    private ItemStack itemStack;
    private int minAmount;
    private int maxAmount;
    private double chance;
    private int minY;
    private boolean fortune;
    private String message;

    public String getName() {
        return name;
    }

    public DropType getDropType() {
        return dropType;
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

    public double getChance() {
        return chance;
    }

    public int getMinY() {
        return minY;
    }

    public boolean isFortune() {
        return fortune;
    }

    public String getMessage() {
        return message;
    }

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

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setFortune(boolean fortune) {
        this.fortune = fortune;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDropType(DropType dropType) {
        this.dropType = dropType;
    }
}
