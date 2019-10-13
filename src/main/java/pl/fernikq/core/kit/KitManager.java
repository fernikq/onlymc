package pl.fernikq.core.kit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KitManager {

    private final CorePlugin plugin;
    private File kitFile;
    private Set<Kit> kits;

    public KitManager(CorePlugin plugin){
        this.plugin = plugin;
        this.kits = new HashSet<>();
        checkFile();
        loadKits();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        kitFile = new File(this.plugin.getDataFolder(), "kits.yml");
        if(!kitFile.exists()){
            this.plugin.saveResource("kits.yml", true);
        }
    }

    public void loadKits(){
        this.kits.clear();
        ConfigurationSection configurationSection = getAutoMessageFile().getConfigurationSection("Kits");
        for(String s : configurationSection.getKeys(false)){
            List<KitItem> kitItemList = new ArrayList<>();
            ConfigurationSection kitCfg = configurationSection.getConfigurationSection(s);
            String[] kitItemData = kitCfg.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(kitItemData[0]), 1, (short) Short.parseShort(kitItemData[1]));
            String kitName = kitCfg.getString("name");
            long time = System.currentTimeMillis() + TimeUtil.getTime(kitCfg.getString("time"));
            UserGroup group = UserGroup.getByName(kitCfg.getString("group"));
            if(group == null){
                group = UserGroup.PLAYER;
            }
            Kit kit = new Kit(itemStack, kitName, time, group);
            ConfigurationSection configuration = kitCfg.getConfigurationSection("items");
            for(String items : configuration.getKeys(false)){
                ConfigurationSection itemCfg = configuration.getConfigurationSection(items);
                String[] itemData = itemCfg.getString("item").split(":");
                ItemStack item = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
                ItemBuilder itemBuilder = new ItemBuilder(item);
                itemBuilder.setAmount(itemCfg.getInt("amount"));
                if(itemCfg.getString("name") != null){
                    itemBuilder.setName(ChatUtil.fixColor(itemCfg.getString("name")));
                }
                if(itemCfg.getString("lore") != null){
                    itemBuilder.setLore(ChatUtil.fixColor(itemCfg.getStringList("lore")));
                }
                if(itemCfg.getString("enchant") != null){
                    itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(itemCfg.getString("enchant")));
                }
                KitItem kitItem = new KitItem(itemBuilder.toItemStack(), itemCfg.getBoolean("separate"));
                kitItemList.add(kitItem);
                kit.setItems(kitItemList);
            }
            this.kits.add(kit);
        }
    }

    public void giveItems(Player player, Kit kit){
        for(KitItem items : kit.getItems()){
            ItemUtil.giveItems(player, items.getItemStack());
        }
    }

    public YamlConfiguration getAutoMessageFile() {
        return YamlConfiguration.loadConfiguration(kitFile);
    }
}
