package pl.fernikq.core.drop;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.EnchantManager;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DropManager {

    private final CorePlugin plugin;
    private File dropFile;
    private List<Drop> drops;

    private ItemStack premiumCaseItem;
    private ItemStack cobblexItem;

    private String premiumCaseNameInGUI;
    private String cobblexNameInGUI;

    private int maxItemsInOneCase;

    public DropManager(CorePlugin plugin){
        this.plugin = plugin;
        this.drops = new ArrayList<>();
        checkFile();
        loadDrops();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        dropFile = new File(this.plugin.getDataFolder(), "drop.yml");
        if(!dropFile.exists()){
            this.plugin.saveResource("drop.yml", true);
        }
    }

    public void loadDrops(){
        YamlConfiguration configuration = getDropFile();
        ItemBuilder premiumCase = new ItemBuilder(ItemUtil.getMaterial(configuration.getString("PremiumCase.item").split(":")[0]))
                .setDurability((short) Short.parseShort(configuration.getString("PremiumCase.item").split(":")[1]))
                .setName(ChatUtil.fixColor(configuration.getString("PremiumCase.name")));
        if(configuration.getString("PremiumCase.lore") != null){
            premiumCase.setLore(ChatUtil.fixColor(configuration.getStringList("PremiumCase.lore")));
        }
        if(configuration.getString("PremiumCase.enchant") != null){
            premiumCase.setEnchant(ItemUtil.getEnchantsFromString(configuration.getString("PremiumCase.enchant")));
        }
        this.premiumCaseNameInGUI = configuration.getString("PremiumCase.nameInGUI");
        this.maxItemsInOneCase = configuration.getInt("PremiumCase.maxItemsInOneCase");
        this.premiumCaseItem = premiumCase.toItemStack();

        ItemBuilder cobblex = new ItemBuilder(ItemUtil.getMaterial(configuration.getString("Cobblex.item").split(":")[0]))
                .setDurability((short) Short.parseShort(configuration.getString("Cobblex.item").split(":")[1]))
                .setName(ChatUtil.fixColor(configuration.getString("Cobblex.name")));
        if(configuration.getString("Cobblex.lore") != null){
            cobblex.setLore(ChatUtil.fixColor(configuration.getStringList("Cobblex.lore")));
        }
        if(configuration.getString("Cobblex.enchant") != null){
            cobblex.setEnchant(ItemUtil.getEnchantsFromString(configuration.getString("Cobblex.enchant")));
        }
        this.cobblexNameInGUI = configuration.getString("Cobblex.nameInGUI");
        this.cobblexItem = cobblex.toItemStack();

        ConfigurationSection configurationSection = configuration.getConfigurationSection("StoneDrop");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            Drop drop = new Drop();
            drop.setDropType(DropType.STONE);
            drop.setChance(configuration.getDouble("chance"));
            drop.setFortune(configuration.getBoolean("fortune"));
            drop.setMinAmount(configuration.getInt("amount.min"));
            drop.setMaxAmount(configuration.getInt("amount.max"));
            drop.setMinY(configuration.getInt("minY"));
            drop.setMessage(configuration.getString("message"));
            String[] itemData = section.getString("item").split(":");
            ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
            ItemBuilder itemBuilder = new ItemBuilder(item);
            if(section.getString("name") != null){
                itemBuilder.setName(ChatUtil.fixColor(section.getString("name")));
            }
            if(section.getString("lore") != null){
                itemBuilder.setLore(ChatUtil.fixColor(section.getStringList("lore")));
            }
            if(section.getString("enchant") != null){
                itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(section.getString("enchant")));
            }
            if(section.getString("nameInGUI") != null){
                drop.setName(section.getString("nameInGUI"));
            }
            this.drops.add(drop);
        }
        configurationSection = configuration.getConfigurationSection("CobblexDrop");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            Drop drop = new Drop();
            drop.setDropType(DropType.COBBLEX);
            drop.setMinAmount(configuration.getInt("amount.min"));
            drop.setMaxAmount(configuration.getInt("amount.max"));
            drop.setMessage(configuration.getString("message"));
            String[] itemData = section.getString("item").split(":");
            ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
            ItemBuilder itemBuilder = new ItemBuilder(item);
            if(section.getString("name") != null){
                itemBuilder.setName(ChatUtil.fixColor(section.getString("name")));
            }
            if(section.getString("lore") != null){
                itemBuilder.setLore(ChatUtil.fixColor(section.getStringList("lore")));
            }
            if(section.getString("enchant") != null){
                itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(section.getString("enchant")));
            }
            if(section.getString("nameInGUI") != null){
                drop.setName(section.getString("nameInGUI"));
            }
            this.drops.add(drop);
        }
        configurationSection = configuration.getConfigurationSection("PremiumCaseDrop");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            Drop drop = new Drop();
            drop.setDropType(DropType.PREMIUMCASE);
            drop.setMinAmount(configuration.getInt("amount.min"));
            drop.setMaxAmount(configuration.getInt("amount.max"));
            drop.setChance(configuration.getDouble("chance"));
            drop.setMessage(configuration.getString("OpenCaseItemName"));
            String[] itemData = section.getString("item").split(":");
            ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
            ItemBuilder itemBuilder = new ItemBuilder(item);
            if(section.getString("name") != null){
                itemBuilder.setName(ChatUtil.fixColor(section.getString("name")));
            }
            if(section.getString("lore") != null){
                itemBuilder.setLore(ChatUtil.fixColor(section.getStringList("lore")));
            }
            if(section.getString("enchant") != null){
                itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(section.getString("enchant")));
            }
            if(section.getString("nameInGUI") != null){
                drop.setName(section.getString("nameInGUI"));
            }
            this.drops.add(drop);
        }
    }

    public List<Drop> getDrops(DropType dropType){
        return new ArrayList<>(this.drops).stream().filter(drop -> drop.getDropType().equals(dropType)).collect(Collectors.toList());
    }

    public YamlConfiguration getDropFile() {
        return YamlConfiguration.loadConfiguration(this.dropFile);
    }
}
