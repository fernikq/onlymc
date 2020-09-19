package pl.fernikq.core.boss;

import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.user.User;

import java.util.HashSet;
import java.util.Set;

public class BossDrop {

    private String name;
    private ItemStack itemStack;
    private int minAmount;
    private int maxAmount;
    private double chance;

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

    public double getChance() {
        return chance;
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
}
