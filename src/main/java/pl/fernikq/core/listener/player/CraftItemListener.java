package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.HashSet;
import java.util.Set;

public class CraftItemListener implements Listener {

    private final CorePlugin plugin;
    private Set<Material> diamondItems;

    public CraftItemListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.diamondItems = new HashSet<>();
        this.diamondItems.add(Material.DIAMOND_HELMET);
        this.diamondItems.add(Material.DIAMOND_CHESTPLATE);
        this.diamondItems.add(Material.DIAMOND_LEGGINGS);
        this.diamondItems.add(Material.DIAMOND_BOOTS);
        this.diamondItems.add(Material.DIAMOND_SWORD);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if(event.getInventory().getType() != InventoryType.WORKBENCH){
            return;
        }
        if(event.getInventory().getResult() == null){
            return;
        }
        ItemStack result = event.getInventory().getResult();
        Player player = (Player)event.getWhoClicked();
        if(result.getType() == Material.JUKEBOX){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, MessagesManager.error("Tego przedmiotu nie mozna stworzyc!"));
            return;
        }
    }

    @EventHandler
    public void onDiamondItemsCraft(CraftItemEvent event){
        if(event.getInventory().getType() != InventoryType.WORKBENCH){
            return;
        }
        if(event.getInventory().getResult() == null){
            return;
        }
        ItemStack result = event.getInventory().getResult();
        Player player = (Player)event.getWhoClicked();
        if(diamondItems.contains(result.getType()) && ConfigManager.diamondItemsBlockTime > System.currentTimeMillis()){
            ChatUtil.sendMessage(player, MessagesManager.error("Diamentowe przedmioty wylaczone sa jeszcze przez "+ TimeUtil.getTimeToString(ConfigManager.diamondItemsBlockTime - System.currentTimeMillis())));
            event.setCancelled(true);
            return;
        }
    }
}
