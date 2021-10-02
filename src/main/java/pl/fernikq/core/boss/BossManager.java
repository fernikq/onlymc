package pl.fernikq.core.boss;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.io.File;
import java.util.*;

public class BossManager {

    private final CorePlugin plugin;
    private File bossFile;
    private List<BossDrop> bossDrops;

    private String giantBossName;
    private double giantBossHealth;

    public BossManager(CorePlugin plugin){
        this.plugin = plugin;
        this.bossDrops = new ArrayList<>();
        checkFile();
        loadBoss();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        bossFile = new File(this.plugin.getDataFolder(), "boss.yml");
        if(!bossFile.exists()){
            this.plugin.saveResource("boss.yml", true);
        }
    }

    public void reload(){
        checkFile();
        loadBoss();
    }

    private void loadBoss(){
        this.bossDrops.clear();
        YamlConfiguration configuration = this.getBossFile();
        this.giantBossName = configuration.getString("BossName");
        this.giantBossHealth = configuration.getDouble("BossHealth");
        ConfigurationSection configurationSection = configuration.getConfigurationSection("Drop");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            BossDrop bossDrop = new BossDrop();
            bossDrop.setMaxAmount(section.getInt("amount.max"));
            bossDrop.setMinAmount(section.getInt("amount.min"));
            bossDrop.setChance(section.getDouble("chance"));
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
                bossDrop.setName(section.getString("nameInGUI"));
            }
            bossDrop.setItemStack(itemBuilder.toItemStack());
            this.bossDrops.add(bossDrop);
        }
    }

    public YamlConfiguration getBossFile() {
        return YamlConfiguration.loadConfiguration(this.bossFile);
    }

    public List<BossDrop> getBossDrops() {
        return new ArrayList<>(this.bossDrops);
    }

    public String getGiantBossName() {
        return giantBossName;
    }

    public double getGiantBossHealth() {
        return giantBossHealth;
    }
}
