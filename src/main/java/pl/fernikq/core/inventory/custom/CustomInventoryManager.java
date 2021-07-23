package pl.fernikq.core.inventory.custom;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.simpleCommand.SimpleCustomCommand;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.NumberUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class CustomInventoryManager {

    private final CorePlugin plugin;
    private File file;
    private final Map<String, CustomInventory> inventoriesNameMap = new HashMap<>();

    public CustomInventoryManager(CorePlugin plugin){
        this.plugin = plugin;
        this.checkFile();
        this.loadInventories();
    }

    private void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        file = new File(this.plugin.getDataFolder(), "customInventories.yml");
        if(!file.exists()){
            this.plugin.saveResource("customInventories.yml", true);
        }
    }

    private void loadInventories(){
        ConfigurationSection configurationSection = this.getFile().getConfigurationSection("Inventories");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection configuration = configurationSection.getConfigurationSection(s);
            Map<Integer, String> commandMap = new HashMap<>();
            String inventoryName = configuration.getString("inventoryName");
            String commandName = configuration.getString("commandName");
            String blankSlots = configuration.getString("blankSlots");
            int inventoryRows = configuration.getInt("inventoryRows");
            InventoryGUI inventoryGUI = new InventoryGUI(inventoryName, inventoryRows, true);
            for(String s1  : configuration.getConfigurationSection("slots").getKeys(false)){
                ConfigurationSection configuration1 = configuration.getConfigurationSection("slots").getConfigurationSection(s1);
                Integer slot = NumberUtil.getIntegerFromString(s1);
                if(Objects.isNull(slot)) continue;
                String[] itemInfo = configuration1.getString("item").split(":");
                String itemCommand = configuration1.getString("command");
                String itemName = configuration1.getString("itemName");
                List<String> itemLore = configuration1.getStringList("itemLore");
                int amount = configuration1.getInt("amount");
                ItemBuilder itemBuilder = new ItemBuilder(ItemUtil.getMaterial(itemInfo[0])).setDurability(Short.parseShort(itemInfo[1])).setAmount(amount);
                if(Objects.nonNull(itemName)){
                    itemBuilder.setName(ChatUtil.fixColor(itemName));
                }
                if(Objects.nonNull(itemLore)){
                    itemBuilder.setLore(ChatUtil.fixColor(itemLore));
                }
                if(Objects.nonNull(itemCommand)){
                    commandMap.put(slot, itemCommand);
                }
                inventoryGUI.setItem(slot, itemBuilder.toItemStack());
            }
            if(Objects.nonNull(blankSlots)){
                String[] itemInfo = blankSlots.split(":");
                inventoryGUI.setEmptyItem(new ItemBuilder(ItemUtil.getMaterial(itemInfo[0])).setDurability(Short.parseShort(itemInfo[1])).setName(" ").toItemStack());
            }
            CustomInventory customInventory = new CustomInventory(inventoryGUI, commandMap);
            CustomInventoryCommand customInventoryCommand = new CustomInventoryCommand(commandName, Arrays.asList(), UserGroup.PLAYER, this.plugin, customInventory);
            customInventoryCommand.register();
            this.inventoriesNameMap.put(inventoryGUI.getInventory().getName(), customInventory);
        }
    }

    public CustomInventory getCustomInventoryByName(String inventoryName){
        return this.inventoriesNameMap.get(inventoryName);
    }

    public YamlConfiguration getFile() {
        return YamlConfiguration.loadConfiguration(file);
    }

}
