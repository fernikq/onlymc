package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.EnchantManager;
import pl.fernikq.core.util.NumberUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.*;

public class EnchantItemListener implements Listener {

    private final CorePlugin plugin;

    public EnchantItemListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchantmentIntegerMap = event.getEnchantsToAdd();
        Map<Enchantment, Integer> enchantmentLimits = ConfigManager.getEnchantmentIntegerMap();
        Block block = event.getEnchantBlock();
        for(Map.Entry entry : enchantmentIntegerMap.entrySet()){
            if(enchantmentLimits.containsKey(entry.getKey()) && enchantmentLimits.get(entry.getKey()) < (int) entry.getValue()){
                enchantmentIntegerMap.put((Enchantment) entry.getKey(), enchantmentLimits.get(entry.getKey()));
            }
        }
    }
}
