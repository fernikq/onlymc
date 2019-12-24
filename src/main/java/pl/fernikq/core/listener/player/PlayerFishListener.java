package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.quests.QuestType;

public class PlayerFishListener implements Listener {

    private final CorePlugin plugin;

    public PlayerFishListener( CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event){
        Player player = event.getPlayer();
        if(!(event.getCaught() instanceof Item)){
            return;
        }
        Item item = (Item) event.getCaught();
        if(item.getItemStack().getType() == Material.RAW_FISH){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                user.getUserStat().setCatchedFishes(user.getUserStat().getCatchedFishes() + 1);
                this.plugin.runAsync(() -> this.plugin.getQuestManager().checkQuest(user, QuestType.CATCHED_FISH));
            });
        }
    }
}
