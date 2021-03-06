package pl.fernikq.core.listener.inventory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.custom.CustomInventory;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventoryClickListener implements Listener {

    private final CorePlugin plugin;
    private Cache<UUID, Long> cooldown;

    public InventoryClickListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.cooldown = CacheBuilder.newBuilder().expireAfterWrite(300, TimeUnit.MILLISECONDS).build();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }
        Player player = (Player)event.getWhoClicked();
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            InventoryGUI inventoryGUI = user.getInventories().get(ChatUtil.fixColor(event.getInventory().getName()));
            if(inventoryGUI != null){
                if(inventoryGUI.isCancelling()){
                    event.setCancelled(true);
                }
                if(cooldown.asMap().containsKey(player.getUniqueId()) && ConfigManager.limitInventoryClicks){
                    ChatUtil.sendMessage(player, MessagesManager.error("Nie klikaj tak szybko w ekwipunek!"));
                    return;
                }
                if(ConfigManager.limitInventoryClicks){
                    cooldown.put(player.getUniqueId(), 300L);
                }
                InventoryAction action = inventoryGUI.getActions().get(event.getRawSlot());
                if(action != null){
                    action.execute(player, event.getInventory(), event.getRawSlot(), event.getInventory().getItem(event.getRawSlot()), event);
                }
                return;
            }
            Map<String, InventoryGUI> abyssInventories = new HashMap<>(this.plugin.getAbyssManager().getInventories());
            inventoryGUI = abyssInventories.get(ChatUtil.fixColor(event.getInventory().getName()));
            if(inventoryGUI != null){
                InventoryAction action = inventoryGUI.getActions().get(event.getRawSlot());
                if(action != null){
                    action.execute(player, event.getInventory(), event.getRawSlot(), event.getInventory().getItem(event.getRawSlot()), event);
                }
                return;
            }
        });
        CustomInventory customInventory = this.plugin.getCustomInventoryManager().getCustomInventoryByName(ChatUtil.fixColor(event.getInventory().getName()));
        if(Objects.nonNull(customInventory)){
            if(customInventory.getInventoryGUI().isCancelling()){
                event.setCancelled(true);
            }
            String command = customInventory.getCommandAtSlot(event.getRawSlot());
            if(Objects.nonNull(command)){
                this.plugin.getServer().dispatchCommand(player, command);
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnvil(InventoryClickEvent event) {
        HumanEntity who = event.getWhoClicked();
        if (!(who instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory)) {
            return;
        }
        if(event.getSlot() != 2){
            return;
        }
        ItemStack itemStack = event.getInventory().getItem(2);
        if(itemStack == null){
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.getDisplayName() == null){
            return;
        }
        event.setCancelled(true);
        ChatUtil.sendMessage((Player)who, MessagesManager.error("Nie mozesz nazwac przedmiotu w kowadle!"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilEnchant(InventoryClickEvent event) {
        HumanEntity who = event.getWhoClicked();
        if(event.isCancelled()){
            return;
        }
        if (!(who instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory)) {
            return;
        }
        if(event.getSlot() != 2){
            return;
        }
        ItemStack itemStack = event.getInventory().getItem(2);
        if(itemStack == null){
            return;
        }
        Map<Enchantment, Integer> enchantmentIntegerMap = itemStack.getEnchantments();
        Map<Enchantment, Integer> enchantmentLimits = ConfigManager.getEnchantmentIntegerMap();
        for(Map.Entry entry : enchantmentIntegerMap.entrySet()){
            if(enchantmentLimits.containsKey(entry.getKey()) && enchantmentLimits.get(entry.getKey()) < (int) entry.getValue()){
                ChatUtil.sendMessage(who, "&8>> &fPoziom enchantu {c}"+((Enchantment)entry.getKey()).getName()+" &fzostal ustawiony na limit {c}"+enchantmentLimits.get(entry.getKey()));
                new ItemBuilder(itemStack).addEnchant((Enchantment)entry.getKey(), enchantmentLimits.get(entry.getKey()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVillagerTrade(InventoryClickEvent event) {
        HumanEntity who = event.getWhoClicked();
        if(event.isCancelled()) {
            return;
        }
        if(!(who instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if(!(inventory instanceof MerchantInventory)) {
            return;
        }
        event.setCancelled(true);
        ChatUtil.sendMessage((Player)who, MessagesManager.error("Handel z wiesniakami zostal wylaczony!"));
    }
}
