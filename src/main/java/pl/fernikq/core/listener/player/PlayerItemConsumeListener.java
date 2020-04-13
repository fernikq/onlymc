package pl.fernikq.core.listener.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.GeneratorType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerItemConsumeListener implements Listener {

    private final CorePlugin plugin;
    public static Cache<UUID, Long> cache;

    public PlayerItemConsumeListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        ItemStack antiTrap = this.plugin.getGeneratorManager().getGenerator(GeneratorType.ANTITRAP).getItemStack();
        if(ItemUtil.isSimilar(itemStack, antiTrap, true)){
            event.setCancelled(true);
            if(this.cache.asMap().containsKey(player.getUniqueId())){
                ChatUtil.sendMessage(player, MessagesManager.error("Efekt antynog nadal trwa!"));
                return;
            }
            this.cache.put(player.getUniqueId(), 1L);
            ItemUtil.removeFromHand(player, 1);
            ChatUtil.sendMessage(player, "&8[&eAntynogi&8] &fUzyles antynog! Jesli ktos cie uderzy, zostaniesz do niego przeteleportowany!");
            return;
        }
    }
}
