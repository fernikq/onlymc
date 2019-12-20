package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

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
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canBuild(user, block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            if(regionFeedback.equals(RegionFeedback.DENY_BUILD_GUILD_CAUSE_EXPLOSION)){
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage().replace("{TIME}", TimeUtil.getTimeToString(user.getGuild().getRegion().getLastExplodeTime() - System.currentTimeMillis())));
                return;
            }
            if(regionFeedback.equals(RegionFeedback.DENY_BUILD_PVP_Y)){
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage().replace("{Y}", Integer.toString(ConfigManager.blockBuildingBelowYDuringFight)));
                return;
            }
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        Generator generator = this.plugin.getGeneratorManager().getGenerator(player.getItemInHand());
        if(generator != null){
            switch(generator.getGeneratorType()){
                case SAND_GENERATOR:{
                    Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            if(guild != null && guild.getRegion().isInCenter(toChange.getLocation())){
                                cancel();
                                return;
                            }
                            toChange.setType(Material.SAND);
                        }
                    }.runTaskTimer(this.plugin, 0, 0);
                    return;
                }
                case OBSIDIAN_GENERATOR:{
                    Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            if(guild != null && guild.getRegion().isInCenter(toChange.getLocation())){
                                cancel();
                                return;
                            }
                            toChange.setType(Material.OBSIDIAN);
                        }
                    }.runTaskTimer(this.plugin, 0, 0);
                    return;
                }
                case BLOCK_BREAKER:{
                    Guild guild = this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).getOrNull();
                    new BukkitRunnable(){
                        int subtract = 0;
                        @Override
                        public void run() {
                            Block toChange = block.getLocation().subtract(0, subtract++, 0).getBlock();
                            if(toChange.getType() == Material.BEDROCK){
                                cancel();
                                return;
                            }
                            if(guild != null && guild.getRegion().isInCenter(toChange.getLocation())){
                                cancel();
                                return;
                            }
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
