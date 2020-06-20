package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.drill.GuildDrill;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;
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
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canBuild(user, block.getLocation(), player.getItemInHand());
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
        if((block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) && block.getLocation().getBlockY() > ConfigManager.chestPlaceMaxY && !user.canByGroup(UserGroup.ADMIN)){
            ChatUtil.sendMessage(player, MessagesManager.error("Stawianie skrzynek zostalo zablokowane powyzej "+ConfigManager.chestPlaceMaxY+" poziomu!"));
            event.setCancelled(true);
            return;
        }
        if(this.plugin.getDrillManager().isSimilar(player.getItemInHand())){
            event.setCancelled(true);
            if(user.canByGroup(UserGroup.ADMIN)){
                if(!user.hasGuild()){
                    ChatUtil.sendMessage(player, MessagesManager.error("Aby postawic wiertlo musisz miec gildie"));
                    return;
                }
                if(!user.getGuild().getRegion().isIn(block.getLocation())){
                    ChatUtil.sendMessage(player, MessagesManager.error("Musisz byc na terenie gildii aby postawic wiertlo"));
                    return;
                }
            }
            if(!this.plugin.getDrillManager().canPlaceDrill(block.getLocation())){
                ChatUtil.sendMessage(player, MessagesManager.error("Aby postawic wiertlo musisz znalezc wiecej wolnego miejsca!"));
                return;
            }
            Guild guild = user.getGuild();
            if(!this.plugin.getDrillManager().canPlaceDrillCauseGuildRegion(block.getLocation(), guild)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz postawic wiertla tak blisko granicy regionu gildii!"));
                return;
            }
            if(!this.plugin.getDrillManager().canPlaceDrillCauseGuildCenter(block.getLocation(), guild)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz postawic wiertla tak blisko centrum gildii!"));
                return;
            }
            if(!this.plugin.getDrillManager().canPlaceDrillCauseOtherGuildDrill(block.getLocation(), guild)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz postawic wiertla tak blisko innego wiertla!"));
                return;
            }
            if(guild.getGuildDrills().size() >= 2){
                ChatUtil.sendMessage(player, MessagesManager.error("Gildia moze posiadac maksymalnie 2 wiertla!"));
                return;
            }
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                this.plugin.getDrillManager().createGuildDrill(block.getLocation());
            }, 1);
            GuildDrill guildDrill = new GuildDrill(guild, block.getLocation().add(0, 1, 0), Material.GOLD_INGOT);
            guild.addDrill(guildDrill);
            this.plugin.getDrillManager().registerDrillTask(guildDrill);
            ChatUtil.sendMessage(player, "&8>> &fPostawiles wiertlo, kliknij na kociol aby nim zarzadzac!");
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDrillManager().getData().insert(guildDrill));
            ItemUtil.removeFromHand(player, 1);
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
                            if(guild != null){
                                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                                    if(guildDrill.isIn(toChange.getLocation())){
                                        cancel();
                                        return;
                                    }
                                }
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
                            if(guild != null){
                                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                                    if(guildDrill.isIn(toChange.getLocation())){
                                        cancel();
                                        return;
                                    }
                                }
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
                            if(guild != null){
                                for(GuildDrill guildDrill : guild.getGuildDrills().values()){
                                    if(guildDrill.isIn(toChange.getLocation())){
                                        cancel();
                                        return;
                                    }
                                }
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
