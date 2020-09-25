package pl.fernikq.core.magiccase;

import org.bukkit.inventory.ItemStack;

public final class MagicCaseDropBuilder {

    private String name;
    private ItemStack itemStack;
    private int minAmount;
    private int maxAmount;

    public static MagicCaseDropBuilder builder() {
        return new MagicCaseDropBuilder();
    }

    public MagicCaseDropBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MagicCaseDropBuilder withItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public MagicCaseDropBuilder withMinAmount(int minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    public MagicCaseDropBuilder withMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public MagicCaseDrop build() {
        MagicCaseDrop magicCaseDrop = new MagicCaseDrop();
        magicCaseDrop.setName(name);
        magicCaseDrop.setItemStack(itemStack);
        magicCaseDrop.setMinAmount(minAmount);
        magicCaseDrop.setMaxAmount(maxAmount);
        return magicCaseDrop;
    }
}
