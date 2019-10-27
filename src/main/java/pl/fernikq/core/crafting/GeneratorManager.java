package pl.fernikq.core.crafting;

import io.vavr.collection.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.kit.KitItem;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class GeneratorManager {

    private final CorePlugin plugin;
    private File generatorFile;
    private List<Generator> generators;

    public GeneratorManager(CorePlugin plugin){
        this.plugin = plugin;
        this.generators = new ArrayList<>();
        checkFile();
        loadGenerators();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        generatorFile = new File(this.plugin.getDataFolder(), "generators.yml");
        if(!generatorFile.exists()){
            this.plugin.saveResource("generators.yml", true);
        }
    }

    public void reload(){
        checkFile();
        loadGenerators();
    }

    public void loadGenerators(){
        this.generators.forEach(generator -> {
            Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
            Recipe recipe;
            while(recipes.hasNext()){
                recipe = recipes.next();
                if(recipe.getResult().equals(generator.getItemStack())){
                    recipes.remove();
                }
            }
        });
        this.generators.clear();
        ConfigurationSection configurationSection = getGeneratorFile().getConfigurationSection("Generators");
        for(String s : configurationSection.getKeys(false)) {
            ConfigurationSection c = configurationSection.getConfigurationSection(s);
            GeneratorType generatorType = GeneratorType.getGeneratorByName(s);
            if(generatorType == null){
                continue;
            }
            List<String> ingredients = c.getStringList("ingredients");
            if(ingredients.size() < 9){
                Logger.warning("Ilosc skladnikow "+s+" sie nie zgadza!");
                continue;
            }
            String guiName = c.getString("guiName");
            String[] itemInfo = c.getString("item.item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(itemInfo[0]), 1, (short) Short.parseShort(itemInfo[1]));
            ItemBuilder builder = new ItemBuilder(itemStack);
            builder.setAmount(c.getInt("item.amount"));
            if(c.getString("item.name") != null){
                builder.setName(ChatUtil.fixColor(c.getString("item.name")));
            }
            if(c.getString("item.lore") != null){
                builder.setLore(ChatUtil.fixColor(c.getStringList("item.lore")));
            }
            if(c.getString("item.enchant") != null){
                builder.setEnchant(ItemUtil.getEnchantsFromString(c.getString("item.enchant")));
            }
            Generator generator = new Generator(generatorType, builder.toItemStack(), ingredients, guiName);
            this.generators.add(generator);
            char c1 = 'a';
            ShapedRecipe shapedRecipe = new ShapedRecipe(generator.getItemStack()).shape("abc", "def", "ghi");
            for(String ingredient : ingredients){
                shapedRecipe.setIngredient(c1, ItemUtil.getMaterial(ingredient));
                c1++;
            }
            Bukkit.addRecipe(shapedRecipe);
        }
    }

    public Generator getGenerator(ItemStack itemStack){
        return HashSet.ofAll(getGenerators()).find(generator -> generator.getItemStack().isSimilar(itemStack)).getOrNull();
    }

    public YamlConfiguration getGeneratorFile() {
        return YamlConfiguration.loadConfiguration(this.generatorFile);
    }

    public List<Generator> getGenerators() {
        return new ArrayList<>(this.generators);
    }
}
