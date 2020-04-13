package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.*;

public class EntityExplodeListener implements Listener {

    private final CorePlugin plugin;

    public EntityExplodeListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event){
        String[] tntHours = ConfigManager.tntHours.split("-");
        int minHour = Integer.parseInt(tntHours[0]);
        int maxHour = Integer.parseInt(tntHours[1]);
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if(currentHour < minHour || currentHour >= maxHour){
            event.setCancelled(true);
            return;
        }
        List<Block> toRemove = new ArrayList<>();
        Set<Guild> guilds = new HashSet<>();
        for(Block block : event.blockList()){
            Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canExplode(block.getLocation(), guild);
            if(!regionFeedback.isPermit()){
                toRemove.add(block);
            }else{
                if(guild != null){
                    guilds.add(guild);
                }
            }
        }
        toRemove.forEach(block -> event.blockList().remove(block));
        event.blockList().forEach(block -> {
            StoneGenerator stoneGenerator = this.plugin.getStoneGeneratorManager().getStoneGenerator(block.getLocation());
            if(stoneGenerator != null){
                this.plugin.getStoneGeneratorManager().deleteGenerator(stoneGenerator);
            }
        });
        guilds.forEach(guild -> {
            guild.getRegion().setLastExplodeTime(System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.guildDenyBuildTimeAfterExplosion));
            guild.getOnlineMembers().forEach(member -> {
                ChatUtil.sendMessage(member.getUser().asPlayer(), MessagesManager.guildTNTMessage.replace("{TIME}", ConfigManager.guildDenyBuildTimeAfterExplosion));
            });
        });
    }
}
