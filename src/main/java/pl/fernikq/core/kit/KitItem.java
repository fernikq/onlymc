package pl.fernikq.core.kit;

import org.bukkit.inventory.ItemStack;

public class KitItem {

    private String name;
    private ItemStack itemStack;
    private boolean separate;

    public KitItem(ItemStack itemStack, boolean separate) {
        this.itemStack = itemStack;
        this.separate = separate;
        this.name = null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public boolean isSeparate() {
        return separate;
    }

    public void setSeparate(boolean separate) {
        this.separate = separate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
