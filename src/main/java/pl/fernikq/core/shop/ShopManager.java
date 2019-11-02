package pl.fernikq.core.shop;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.kit.KitItem;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ShopManager {

    private final CorePlugin plugin;
    private File shopFile;
    private List<Shop> shops;

    public ShopManager(CorePlugin plugin){
        this.plugin = plugin;
        this.shops = new ArrayList<>();
        checkFile();
        loadShops();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        shopFile = new File(this.plugin.getDataFolder(), "shop.yml");
        if(!shopFile.exists()){
            this.plugin.saveResource("shop.yml", true);
        }
    }

    public void reload(){
        checkFile();
        loadShops();
    }

    public void loadShops(){
        this.shops.clear();

        //BUY
        ConfigurationSection configurationSection = getShopFile().getConfigurationSection("Shops.Buy");
        for(String s : configurationSection.getKeys(false)){
            List<ShopItem> shopItemList = new ArrayList<>();
            ConfigurationSection shopCfg = configurationSection.getConfigurationSection(s);
            String[] kitItemData = shopCfg.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(kitItemData[0]), 1, (short) Short.parseShort(kitItemData[1]));
            if(shopCfg.getString("name") != null){
                new ItemBuilder(itemStack).setName(ChatUtil.fixColor(shopCfg.getString("name")));
            }
            if(shopCfg.getString("lore") != null){
                new ItemBuilder(itemStack).setLore(ChatUtil.fixColor(shopCfg.getStringList("lore")));
            }
            Shop shop = new Shop(itemStack, ShopType.BUY);
            ConfigurationSection configuration = shopCfg.getConfigurationSection("items");
            for(String items : configuration.getKeys(false)){
                ConfigurationSection itemCfg = configuration.getConfigurationSection(items);
                String[] itemData = itemCfg.getString("item").split(":");
                ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
                ItemBuilder itemBuilder = new ItemBuilder(item);
                if(itemCfg.getString("name") != null){
                    itemBuilder.setName(ChatUtil.fixColor(itemCfg.getString("name")));
                }
                if(itemCfg.getString("lore") != null){
                    itemBuilder.setLore(ChatUtil.fixColor(itemCfg.getStringList("lore")));
                }
                if(itemCfg.getString("enchant") != null){
                    itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(itemCfg.getString("enchant")));
                }
                ShopItem shopItem = new ShopItem(itemBuilder.toItemStack(), itemCfg.getInt("amount"), itemCfg.getInt("price"));
                if(itemCfg.getString("nameInGUI") != null){
                    shopItem.setName(itemCfg.getString("nameInGUI"));
                }
                shopItemList.add(shopItem);
                shop.setItems(shopItemList);
            }
            this.shops.add(shop);
        }

        //SELL
        configurationSection = getShopFile().getConfigurationSection("Shops.Sell");
        for(String s : configurationSection.getKeys(false)){
            List<ShopItem> shopItemList = new ArrayList<>();
            ConfigurationSection shopCfg = configurationSection.getConfigurationSection(s);
            String[] kitItemData = shopCfg.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(kitItemData[0]), 1, (short) Short.parseShort(kitItemData[1]));
            if(shopCfg.getString("name") != null){
                new ItemBuilder(itemStack).setName(ChatUtil.fixColor(shopCfg.getString("name")));
            }
            if(shopCfg.getString("lore") != null){
                new ItemBuilder(itemStack).setLore(ChatUtil.fixColor(shopCfg.getStringList("lore")));
            }
            Shop shop = new Shop(itemStack, ShopType.SELL);
            ConfigurationSection configuration = shopCfg.getConfigurationSection("items");
            for(String items : configuration.getKeys(false)){
                ConfigurationSection itemCfg = configuration.getConfigurationSection(items);
                String[] itemData = itemCfg.getString("item").split(":");
                ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
                ItemBuilder itemBuilder = new ItemBuilder(item);
                if(itemCfg.getString("name") != null){
                    itemBuilder.setName(ChatUtil.fixColor(itemCfg.getString("name")));
                }
                if(itemCfg.getString("lore") != null){
                    itemBuilder.setLore(ChatUtil.fixColor(itemCfg.getStringList("lore")));
                }
                if(itemCfg.getString("enchant") != null){
                    itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(itemCfg.getString("enchant")));
                }
                ShopItem shopItem = new ShopItem(itemBuilder.toItemStack(), itemCfg.getInt("amount"), itemCfg.getInt("price"));
                if(itemCfg.getString("nameInGUI") != null){
                    shopItem.setName(itemCfg.getString("nameInGUI"));
                }
                shopItemList.add(shopItem);
                shop.setItems(shopItemList);
            }
            this.shops.add(shop);
        }

        //TODO level shop
    }

    public YamlConfiguration getShopFile() {
        return YamlConfiguration.loadConfiguration(shopFile);
    }

    public List<Shop> getShops(ShopType type) {
        return getShops().filter(shop -> shop.getShopType().equals(type)).collect(Collectors.toList());
    }

    public Set<Shop> getShops(){
        return HashSet.ofAll(new ArrayList<>(this.shops));
    }
}
