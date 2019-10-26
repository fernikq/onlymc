package pl.fernikq.core.kit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;

import java.io.File;
import java.util.*;

public class KitManager {

    private final CorePlugin plugin;
    private File kitFile;
    private List<Kit> kits;

    public KitManager(CorePlugin plugin){
        this.plugin = plugin;
        this.kits = new ArrayList<>();
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
        ConfigurationSection configurationSection = getKitFile().getConfigurationSection("Kits");
        for(String s : configurationSection.getKeys(false)){
            List<KitItem> kitItemList = new ArrayList<>();
            ConfigurationSection kitCfg = configurationSection.getConfigurationSection(s);
            String[] kitItemData = kitCfg.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(kitItemData[0]), 1, (short) Short.parseShort(kitItemData[1]));
            String kitName = kitCfg.getString("name");
            long time = TimeUtil.getTime(kitCfg.getString("time"));
            UserGroup group = UserGroup.getByName(kitCfg.getString("group"));
            if(group == null){
                group = UserGroup.PLAYER;
            }
            boolean canRankHigher = kitCfg.getBoolean("canRankHigher");
            Kit kit = new Kit(itemStack, kitName, time, group, canRankHigher);
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
                if(itemCfg.getString("nameInGUI") != null){
                    kitItem.setName(itemCfg.getString("nameInGUI"));
                }
                kitItemList.add(kitItem);
                kit.setItems(kitItemList);
            }
            this.kits.add(kit);
        }
    }

    public void reload(){
        checkFile();
        loadKits();
    }

    public String kitsToString(Map<String, Long> kits) {
        if(kits.isEmpty() || kits == null) {
            return "";
        }
        StringBuilder string = new StringBuilder();
        int i = 0;
        for(Map.Entry<String, Long> e : kits.entrySet()) {
            if(i == 0) {
                string.append(e.getKey() + ":" + e.getValue());
            }else {
                string.append(";" + e.getKey() + ":" + e.getValue());
            }
            i++;
        }
        return string.toString();
    }

    public Map<String, Long> kitsFromString(String data){
        Map<String, Long> kits = new HashMap<String, Long>();
        if(data.isEmpty() || data == null) {
            return kits;
        }
        String[] str = data.split(";");
        for(int i = 0; i < str.length; i++) {
            String s = str[i];
            String[] ss = s.split(":");
            kits.put(ss[0], Long.valueOf(ss[1]));
        }
        return kits;
    }

    public void giveItems(Player player, Kit kit){
        for(KitItem item : kit.getItems()){
            if(item.isSeparate()){
                for(int i = 0; i < item.getItemStack().getAmount(); i++){
                    ItemUtil.giveItems(player, new ItemBuilder(item.getItemStack().clone()).setAmount(1).toItemStack());
                }
                continue;
            }
            ItemUtil.giveItems(player, item.getItemStack());
        }
    }

    public boolean canTakeByTime(User user, Kit kit){
        if(!user.getKitTimes().containsKey(kit.getName())){
            return true;
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return true;
        }
        return user.getKitTimes().get(kit.getName()) < System.currentTimeMillis();
    }

    public boolean canTakeByGroup(User user, Kit kit){
        if(kit.canRankHigher()){
            return user.canByGroup(kit.getGroup());
        }
        if(user.canByGroup(UserGroup.ADMIN)){
            return true;
        }
        return user.getGroup().equals(kit.getGroup());
    }

    public YamlConfiguration getKitFile() {
        return YamlConfiguration.loadConfiguration(kitFile);
    }

    public List<Kit> getKits() {
        return new ArrayList<>(this.kits);
    }
}
