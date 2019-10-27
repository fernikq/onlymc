package pl.fernikq.core.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.util.ItemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean hasItems(Player player){
        for(Map.Entry<Material, Integer> item : getAmounts().entrySet()){
            int amount = ItemUtil.getAmountOfItem(player.getInventory(), item.getKey(), (short) 0);
            if(amount < item.getValue()){
                return false;
            }
        }
        return true;
    }

    public void removeItems(Player player){
        for(Map.Entry<Material, Integer> item : getAmounts().entrySet()){
            ItemUtil.remove(new ItemStack(item.getKey(), 1, (short) 0), player, item.getValue());
        }
    }

    public List<Material> getMaterials(){
        List<Material> materials = new ArrayList<>();
        for(String string : this.ingredients){
            materials.add(ItemUtil.getMaterial(string));
        }
        return materials;
    }

    public Map<Material, Integer> getAmounts(){
        Map<Material, Integer> amounts = new HashMap<>();
        for(Material material : getMaterials()){
            if(amounts.containsKey(material)){
                amounts.put(material, amounts.get(material)+1);
                continue;
            }
            amounts.put(material, 1);
        }
        return amounts;
    }
}
