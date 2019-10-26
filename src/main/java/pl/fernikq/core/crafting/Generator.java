package pl.fernikq.core.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    private GeneratorType generatorType;
    private ItemStack itemStack;
    private List<String> ingredients;
    private String inventoryName;

    public Generator(GeneratorType generatorType, ItemStack itemStack, List<String> ingredients, String inventoryName){
        this.generatorType = generatorType;
        this.itemStack = itemStack;
        this.ingredients = ingredients;
        this.inventoryName = inventoryName;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(GeneratorType generatorType) {
        this.generatorType = generatorType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public List<String> getIngredients() {
        return new ArrayList<>(this.ingredients);
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }
}
