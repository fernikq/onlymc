package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.crafting.GeneratorType;
import pl.fernikq.core.crafting.stoneGenerator.StoneGenerator;
import pl.fernikq.core.drop.Drop;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.logblock.LogBlock;
import pl.fernikq.core.guild.logblock.LogBlockActionType;
import pl.fernikq.core.magiccase.MagicCaseType;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.RandomUtil;
import pl.nsclient.spigot.MCPlugin;
import pl.nsclient.spigot.MCPluginAPI;

import java.util.Map;
import java.util.Objects;

public class BlockBreakListener implements Listener {

    private final CorePlugin plugin;

    public BlockBreakListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(ConfigManager.freezeTime > System.currentTimeMillis() && !user.canByGroup(UserGroup.TEST_HELPER)){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, "&8[&b&lZamrozenie&8] &fNie mozesz niszczyc blokow!");
            return;
        }
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroy(user, block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        StoneGenerator stoneGenerator = this.plugin.getStoneGeneratorManager().getStoneGenerator(block.getLocation());
        if(stoneGenerator != null){
            if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_PICKAXE){
                ChatUtil.sendMessage(player, "&8>> {n}Zniszczyles stoniarke&8!");
                this.plugin.getStoneGeneratorManager().deleteGenerator(stoneGenerator);
                Generator generator = this.plugin.getGeneratorManager().getGenerator(GeneratorType.STONE_GENERATOR);
                ItemUtil.giveItems(player, generator.getItemStack().clone());
                event.setCancelled(true);
                block.setType(Material.AIR);
                return;
            }
            this.plugin.getStoneGeneratorManager().regenGenerator(stoneGenerator);
        }
        if(block.getType() == Material.STONE){
            if(player.getGameMode() != GameMode.SURVIVAL){
                return;
            }
            for(Drop drop : this.plugin.getDropManager().getDrops(DropType.STONE)){
                if(drop.getDisabled().contains(user)){
                    continue;
                }
                double chance = drop.getChance();
                if(user.getUserStat().isTurboDrop()) chance *= ConfigManager.turboDropMultiplier;
                if(MCPlugin.getAuthorizedPlayers().contains(player.getName())) chance *= ConfigManager.turboDropCauseClientMultiplier;
                if(!RandomUtil.getChance(chance)){
                    continue;
                }
                if(block.getLocation().getBlockY() >= drop.getMinY()){
                    continue;
                }
                int amount = getAmountOfDrop(drop, player);
                ItemBuilder itemBuilder = new ItemBuilder(drop.getItemStack().clone()).setAmount(amount);
                if(user.getUserChat().isDropMessages()){
                    ChatUtil.sendMessage(player, drop.getMessage().replace("{AMOUNT}", Integer.toString(amount)));
                }
                ItemUtil.giveItems(player, itemBuilder.toItemStack());
            }
            if(this.plugin.getDropManager().getDisabledCobblestone().contains(user)){
                event.setCancelled(true);
                block.setType(Material.AIR);
                ItemUtil.recalculateDurability(player, player.getItemInHand(), true);
            }
            user.getUserStat().addMinedStone(1);
            this.plugin.getQuestManager().checkQuest(user, QuestType.MINED_STONE);
            user.getUserStat().addMiningExperience(1);
            player.giveExp(user.getUserStat().isTurboExp() ? (ConfigManager.dropStoneExp * (int)ConfigManager.turboExpMultiplier) : ConfigManager.dropStoneExp);
            if(user.getUserStat().getMiningExperience() == user.getUserStat().getLevel() * 260){
                user.getUserStat().addLevel(1);
                ChatUtil.sendMessage(player, MessagesManager.dropLevelupMessage.replace("{LVL}", Integer.toString(user.getUserStat().getLevel())));
            }
            if(RandomUtil.getChance(user.getUserStat().isTurboDrop() ? (ConfigManager.coinsDropFromStoneChance * ConfigManager.turboDropMultiplier) : ConfigManager.coinsDropFromStoneChance)){
                int amount = RandomUtil.getRandInt(Integer.parseInt(ConfigManager.coinsDropFromStoneAmount.split("-")[0]), Integer.parseInt(ConfigManager.coinsDropFromStoneAmount.split("-")[1]));
                user.getUserStat().addCoinsFromStone(amount);
                user.getUserStat().addCoins(amount);
                ChatUtil.sendMessage(player, MessagesManager.coinsDropFromStoneMessage.replace("{AMOUNT}", Integer.toString(amount)));
            }
            for(Map.Entry map : this.plugin.getMagicCaseManager().getMiningChance().entrySet()){
                MagicCaseType magicCaseType = (MagicCaseType)map.getKey();
                if(RandomUtil.getChance((double)map.getValue())){
                    user.getUserStat().addKeyFragmentsByMagicCaseType(magicCaseType, 1);
                    ChatUtil.sendMessage(player, "&8>> &fOtrzymales fragment klucza do skrzyni o typie&8: "+magicCaseType.getName());
                }
            }
        }
        if(block.getType() == Material.OBSIDIAN){
            player.giveExp(user.getUserStat().isTurboExp() ? (ConfigManager.dropObsidianExp * (int)ConfigManager.turboExpMultiplier) : ConfigManager.dropObsidianExp);
        }
        if(block.getType() == Material.LOG_2 || block.getType() == Material.LOG){
            user.getUserStat().setMinedWood(user.getUserStat().getMinedWood() + 1);
            this.plugin.getQuestManager().checkQuest(user, QuestType.MINED_WOOD);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogBlock(BlockBreakEvent event){
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        Guild guild = this.plugin.getGuildManager().getGuildByLocation(location).getOrNull();
        if(Objects.isNull(guild)){
            return;
        }
        LogBlock logBlock = new LogBlock(location, System.currentTimeMillis(), player.getName(), LogBlockActionType.DESTROY_BLOCK, block.getType().name());
        guild.addLogBlockAtLocation(logBlock);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getGuildManager().getLogBlockData().insertLogBlock(logBlock, guild);
        });
    }

    private int getAmountOfDrop(Drop drop, Player player){
        int amount = drop.getMinAmount() == drop.getMaxAmount() ? drop.getMaxAmount() : RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount());
        if(player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && drop.isFortune()){
            int enchantLevel = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if(enchantLevel == 1){
                amount = RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount() + 1);
            }
            if(enchantLevel == 2){
                amount = RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount() + 2);
            }else{
                amount = RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount() + 3);
            }
        }
        return amount;
    }
}
