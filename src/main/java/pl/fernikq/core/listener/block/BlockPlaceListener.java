package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.util.ChatUtil;

public class BlockPlaceListener implements Listener {

    private final CorePlugin plugin;

    public BlockPlaceListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().can(player, block.getLocation(), RegionProtectionType.BUILD);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        Generator generator = this.plugin.getGeneratorManager().getGenerator(player.getItemInHand());
        if(generator != null){
            switch(generator.getGeneratorType()){
                case SAND_GENERATOR:{
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            //TODO Guild region check
                            toChange.setType(Material.SAND);
                        }
                    }.runTaskTimer(this.plugin, 0, 0);
                    return;
                }
                case OBSIDIAN_GENERATOR:{
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            //TODO Guild region check
                            toChange.setType(Material.OBSIDIAN);
                        }
                    }.runTaskTimer(this.plugin, 0, 0);
                    return;
                }
                case BLOCK_BREAKER:{
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            //TODO Guild region check
                            toChange.setType(Material.AIR);
                        }
                    }.runTaskTimer(this.plugin, 0, 0);
                    return;
                }
                case STONE_GENERATOR:{
                    StoneGenerator stoneGenerator = this.plugin.getStoneGeneratorManager().getStoneGenerator(block.getLocation());
                    if(stoneGenerator != null){
                        ChatUtil.sendMessage(player, MessagesManager.error("W tym miejscu znajduje sie juz stoniarka!"));
                        event.setCancelled(true);
                        return;
                    }
                    stoneGenerator = new StoneGenerator(block.getLocation());
                    this.plugin.getStoneGeneratorManager().saveGenerator(stoneGenerator);
                    this.plugin.getStoneGeneratorManager().registerGenerator(stoneGenerator);
                    block.setType(Material.STONE);
                    ChatUtil.sendMessage(player, "&8>> {n}Utworzyles stoniarke&8!");
                    return;
                }
            }
        }
    }
}
