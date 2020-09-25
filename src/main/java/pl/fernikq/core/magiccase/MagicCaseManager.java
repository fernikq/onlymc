package pl.fernikq.core.magiccase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.magiccase.draw.MagicCaseDraw;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MagicCaseManager {

    private final CorePlugin plugin;
    private File magicCaseFile;

    private ConcurrentMap<Location, MagicCase> magicCaseMap;

    private double normalKillingChance;
    private double normalMiningChance;
    private double premiumKillingChance;
    private double premiumMiningChance;
    private int normalFragmentsRequiredToCreate;
    private int premiumFragmentsRequiredToCreate;

    private ItemStack premiumKey;
    private ItemStack normalKey;

    private Map<MagicCaseType, List<MagicCaseDrop>> caseDrop;

    private Set<UUID> playerInDraw;

    public MagicCaseManager(CorePlugin plugin){
        this.plugin = plugin;
        this.magicCaseMap = new ConcurrentHashMap<>();
        this.caseDrop = new HashMap<>();
        this.caseDrop.put(MagicCaseType.NORMAL, new ArrayList<>());
        this.caseDrop.put(MagicCaseType.PREMIUM, new ArrayList<>());
        this.playerInDraw = new HashSet<>();
        checkFile();
        load();
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        magicCaseFile = new File(this.plugin.getDataFolder(), "magiccase.yml");
        if(!magicCaseFile.exists()){
            this.plugin.saveResource("magiccase.yml", true);
        }
    }

    public void reload(){
        checkFile();
        load();
    }

    public void openCase(Player player, MagicCase magicCase){
        //TODO particles
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(Objects.isNull(user) || Objects.isNull(player)){
            ChatUtil.sendMessage(player, MessagesManager.error("Ojojoj, podobno nie istniejesz! Zglos sie do administracji"));
            return;
        }
        UUID uuid = player.getUniqueId();
        MagicCaseDraw magicCaseDraw = new MagicCaseDraw(player, this.plugin.getUserInventory().magicCaseDraw(user, magicCase.getType()));
        AtomicInteger repetitions = new AtomicInteger(0);
        this.playerInDraw.add(uuid);
        magicCaseDraw.getInventoryGUI().openInventory(player);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(Objects.isNull(player) || !player.isOnline()){
                    playerInDraw.remove(uuid);
                    cancel();
                    return;
                }
                if(!player.getInventory().containsAtLeast(magicCase.getType() == MagicCaseType.NORMAL ? normalKey : premiumKey, 1)){
                    cancel();
                    if(player.getInventory() != null && Objects.equals(player.getOpenInventory().getTopInventory(), magicCaseDraw.getInventoryGUI().getInventory())){
                        player.closeInventory();
                    }
                    ChatUtil.sendMessage(player, MessagesManager.error("Straciles klucz! Losowanie zostalo przerwane!"));
                    playerInDraw.remove(uuid);
                    return;
                }
                if(repetitions.get() >= magicCaseDraw.getRepetitions()){
                    player.getInventory().removeItem(magicCase.getType() == MagicCaseType.NORMAL ? normalKey : premiumKey);
                    ItemStack itemStack = magicCaseDraw.getInventoryGUI().getInventory().getItem(13);
                    ItemUtil.giveItems(player, itemStack);
                    ChatUtil.sendMessage(player, "&8>> &fOtworzyles "+(magicCase.getType() == MagicCaseType.NORMAL ? "&bStandardowa" : "&5Wyjatkowa")+" &fi otrzymales wygrany przedmiot!");
                    cancel();
                    playerInDraw.remove(uuid);
                    new BukkitRunnable(){
                        int i = 0;
                        int left = 9;
                        int right = 17;
                        @Override
                        public void run() {
                            if(i >= 4){
                                cancel();
                                return;
                            }
                            if(player.getInventory() == null || !Objects.equals(player.getOpenInventory().getTopInventory(), magicCaseDraw.getInventoryGUI().getInventory())){
                                player.openInventory(magicCaseDraw.getInventoryGUI().getInventory());
                            }
                            magicCaseDraw.getInventoryGUI().setItem(left, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)14).toItemStack());
                            magicCaseDraw.getInventoryGUI().setItem(right, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)14).toItemStack());
                            magicCaseDraw.getInventoryGUI().setItem(left-9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)7).toItemStack());
                            magicCaseDraw.getInventoryGUI().setItem(left+9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)7).toItemStack());
                            magicCaseDraw.getInventoryGUI().setItem(right-9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)7).toItemStack());
                            magicCaseDraw.getInventoryGUI().setItem(right+9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)7).toItemStack());
                            left++;
                            right--;
                            i++;
                        }
                    }.runTaskTimer(plugin, 0, 10);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if(player.getInventory() != null && Objects.equals(player.getOpenInventory().getTopInventory(), magicCaseDraw.getInventoryGUI().getInventory())){
                            player.closeInventory();
                        }
                    }, 60);
                    return;
                }
                if(player.getInventory() == null || !Objects.equals(player.getOpenInventory().getTopInventory(), magicCaseDraw.getInventoryGUI().getInventory())){
                    player.openInventory(magicCaseDraw.getInventoryGUI().getInventory());
                }
                repetitions.getAndIncrement();
                player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                for(int i = 9; i < 17; i++){
                    magicCaseDraw.getInventoryGUI().setItem(i, magicCaseDraw.getInventoryGUI().getInventory().getItem(i+1));
                }
                MagicCaseDrop magicCaseDrop = getCaseDrop(magicCase.getType()).get(RandomUtil.getRandInt(0, getCaseDrop(magicCase.getType()).size() - 1));
                ItemStack itemStack = new ItemBuilder(magicCaseDrop.getItemStack()).setAmount(RandomUtil.getRandInt(magicCaseDrop.getMinAmount(), magicCaseDrop.getMaxAmount())).toItemStack();
                magicCaseDraw.getInventoryGUI().setItem(17, itemStack);
            }
        }.runTaskTimer(this.plugin, 15, 15);
    }

    public void addCase(Location location, MagicCase magicCase){
        this.magicCaseMap.put(location, magicCase);
    }

    public void removeCase(Location location){
        this.magicCaseMap.remove(location);
    }

    public void load(){
        this.caseDrop.forEach((magicCaseType, magicCaseDrops) -> magicCaseDrops.clear());
        YamlConfiguration configuration = this.getCaseFille();
        this.normalKillingChance = configuration.getDouble("DropChance.normal.killing");
        this.normalMiningChance = configuration.getDouble("DropChance.normal.mining");
        this.premiumKillingChance = configuration.getDouble("DropChance.premium.killing");
        this.premiumMiningChance = configuration.getDouble("DropChance.premium.mining");
        this.normalFragmentsRequiredToCreate = configuration.getInt("Key.normal.fragmentsRequiredToCreate");
        this.premiumFragmentsRequiredToCreate = configuration.getInt("Key.premium.fragmentsRequiredToCreate");
        this.normalKey = new ItemBuilder(ItemUtil.getMaterial(configuration.getString("Key.normal.item").split(":")[0]))
                .setDurability(Short.parseShort(configuration.getString("Key.normal.item").split(":")[1]))
                .setName(ChatUtil.fixColor(configuration.getString("Key.normal.name"))).setLore(ChatUtil.fixColor(configuration.getStringList("Key.normal.lore")))
                .setEnchant(ItemUtil.getEnchantsFromString(configuration.getString("Key.normal.enchant"))).toItemStack();
        this.premiumKey = new ItemBuilder(ItemUtil.getMaterial(configuration.getString("Key.premium.item").split(":")[0]))
                .setDurability(Short.parseShort(configuration.getString("Key.premium.item").split(":")[1]))
                .setName(ChatUtil.fixColor(configuration.getString("Key.premium.name"))).setLore(ChatUtil.fixColor(configuration.getStringList("Key.premium.lore")))
                .setEnchant(ItemUtil.getEnchantsFromString(configuration.getString("Key.premium.enchant"))).toItemStack();
        ConfigurationSection configurationSection = configuration.getConfigurationSection("Drop.normal");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            MagicCaseDrop magicCaseDrop = MagicCaseDropBuilder.builder().withName(section.getString("nameInGUI"))
                    .withMinAmount(section.getInt("amount.min")).withMaxAmount(section.getInt("amount.max")).build();
            String[] itemData = section.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
            ItemBuilder itemBuilder = new ItemBuilder(itemStack);
            if(section.getString("name") != null){
                itemBuilder.setName(ChatUtil.fixColor(section.getString("name")));
            }
            if(section.getString("lore") != null){
                itemBuilder.setLore(ChatUtil.fixColor(section.getStringList("lore")));
            }
            if(section.getString("enchant") != null){
                itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(section.getString("enchant")));
            }
            magicCaseDrop.setItemStack(itemStack);
            addDrop(MagicCaseType.NORMAL, magicCaseDrop);
        }
        configurationSection = configuration.getConfigurationSection("Drop.premium");
        for(String s : configurationSection.getKeys(false)){
            ConfigurationSection section = configurationSection.getConfigurationSection(s);
            MagicCaseDrop magicCaseDrop = MagicCaseDropBuilder.builder().withName(section.getString("nameInGUI"))
                    .withMinAmount(section.getInt("amount.min")).withMaxAmount(section.getInt("amount.max")).build();
            String[] itemData = section.getString("item").split(":");
            ItemStack itemStack = new ItemStack(ItemUtil.getMaterial(itemData[0]), 1, (short) Short.parseShort(itemData[1]));
            ItemBuilder itemBuilder = new ItemBuilder(itemStack);
            if(section.getString("name") != null){
                itemBuilder.setName(ChatUtil.fixColor(section.getString("name")));
            }
            if(section.getString("lore") != null){
                itemBuilder.setLore(ChatUtil.fixColor(section.getStringList("lore")));
            }
            if(section.getString("enchant") != null){
                itemBuilder.setEnchant(ItemUtil.getEnchantsFromString(section.getString("enchant")));
            }
            magicCaseDrop.setItemStack(itemStack);
            addDrop(MagicCaseType.PREMIUM, magicCaseDrop);
        }
    }

    public double getNormalKillingChance() {
        return normalKillingChance;
    }

    public double getNormalMiningChance() {
        return normalMiningChance;
    }

    public double getPremiumKillingChance() {
        return premiumKillingChance;
    }

    public double getPremiumMiningChance() {
        return premiumMiningChance;
    }

    public int getNormalFragmentsRequiredToCreate() {
        return normalFragmentsRequiredToCreate;
    }

    public int getPremiumFragmentsRequiredToCreate() {
        return premiumFragmentsRequiredToCreate;
    }

    public ItemStack getPremiumKey() {
        return premiumKey;
    }

    public ItemStack getNormalKey() {
        return normalKey;
    }

    private void addDrop(MagicCaseType magicCaseType, MagicCaseDrop magicCaseDrop){
        this.caseDrop.get(magicCaseType).add(magicCaseDrop);
    }

    public YamlConfiguration getCaseFille() {
        return YamlConfiguration.loadConfiguration(this.magicCaseFile);
    }

    public List<MagicCaseDrop> getCaseDrop(MagicCaseType magicCaseType) {
        return new ArrayList<>(this.caseDrop.get(magicCaseType));
    }

    public ConcurrentMap<Location, MagicCase> getMagicCaseMap() {
        return new ConcurrentHashMap<>(this.magicCaseMap);
    }

    public Set<UUID> getPlayerInDraw() {
        return new HashSet<>(this.playerInDraw);
    }
}
