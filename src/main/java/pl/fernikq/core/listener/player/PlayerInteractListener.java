package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.drop.Drop;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.region.GuildRegion;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class PlayerInteractListener implements Listener {

    private final CorePlugin plugin;
    List<Material> vehicles;

    public PlayerInteractListener(CorePlugin plugin){
        this.plugin = plugin;
        this.vehicles = Arrays.asList(Material.BOAT, Material.MINECART, Material.COMMAND_MINECART, Material.EXPLOSIVE_MINECART,
                Material.HOPPER_MINECART, Material.POWERED_MINECART, Material.STORAGE_MINECART);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(player.getItemInHand() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && this.vehicles.contains(player.getItemInHand().getType())){
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(user, block.getLocation(), RegionProtectionType.VEHICLES);
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
                return;
            }
            return;
        }
        if(block != null && block.getTypeId() == 60 && event.getAction() == Action.PHYSICAL){
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroyFarmlands(user, block.getLocation());
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(block.getType() == Material.TNT && event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand() != null && player.getItemInHand().getType() == Material.FLINT_AND_STEEL){
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(user, block.getLocation(), RegionProtectionType.IGNITE_TNT);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(this.plugin.getDropManager().getCobblexItem().isSimilar(player.getItemInHand())){
            Drop drop = (Drop)this.plugin.getDropManager().getDrops(DropType.COBBLEX).get(RandomUtil.getRandInt(0, this.plugin.getDropManager().getDrops(DropType.COBBLEX).size() - 1));
            int amount = drop.getMinAmount() == drop.getMaxAmount() ? drop.getMaxAmount() : RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount());
            ItemBuilder itemBuilder = new ItemBuilder(drop.getItemStack().clone()).setAmount(amount);
            ItemUtil.giveItems(player, itemBuilder.toItemStack());
            ChatUtil.sendMessage(player, drop.getMessage().replace("{AMOUNT}", Integer.toString(amount)));
            event.setCancelled(true);
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> user.getUserStat().addOpenedCobblex(1));
            ItemUtil.removeFromHand(player, 1);
            return;
        }
        if(this.plugin.getDropManager().getPremiumCaseItem().isSimilar(player.getItemInHand())){
            int items = 0;
            List<String> dropMessages = new ArrayList<>();
            for(Drop drop : this.plugin.getDropManager().getDrops(DropType.PREMIUMCASE)){
                if(items >= this.plugin.getDropManager().getMaxItemsInOneCase()){
                    break;
                }
                if(!RandomUtil.getChance(drop.getChance())){
                    continue;
                }
                int amount = drop.getMinAmount() == drop.getMaxAmount() ? drop.getMaxAmount() : RandomUtil.getRandInt(drop.getMinAmount(), drop.getMaxAmount());
                ItemBuilder itemBuilder = new ItemBuilder(drop.getItemStack().clone()).setAmount(amount);
                ItemUtil.giveItems(player, itemBuilder.toItemStack());
                dropMessages.add(" &8- {n}"+drop.getMessage().replace("{AMOUNT}", Integer.toString(amount)).replace("{CHANCE}", Double.toString(drop.getChance())));
                items++;
            }
            event.setCancelled(true);
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> user.getUserStat().addOpenedPremiumCase(1));
            ItemUtil.removeFromHand(player, 1);
            if(items == 0){
                ChatUtil.sendMessage(player, "&8>> {n}Otworzyles "+this.plugin.getDropManager().getPremiumCaseItem().getItemMeta().getDisplayName()+" {n}ale niestety nic nie wypadlo :(");
                return;
            }
            Bukkit.getOnlinePlayers().forEach(online -> {
                this.plugin.getUserManager().getUser(online.getUniqueId()).filter(user -> user.getUserChat().isPremiumCaseMessages()).peek(user -> {
                    ChatUtil.sendMessage(online, "{n}Gracz {c}"+player.getName()+" {n}otworzyl "+this.plugin.getDropManager().getPremiumCaseItem().getItemMeta().getDisplayName()+" {n}i otrzymal&8:", " ");
                    for(String string : dropMessages){
                        ChatUtil.sendMessage(online, string);
                    }
                    ChatUtil.sendMessage(online, " ");
                });
            });
            return;
        }
        if(block != null && block.getType() == Material.ENDER_PORTAL_FRAME){
            if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).peek(guild -> {
                    if(!guild.getRegion().getCenter().equals(block.getLocation())) {
                        return;
                    }
                    event.setCancelled(true);
                    if(player.getGameMode() != GameMode.SURVIVAL){
                        return;
                    }
                    User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
                    if(user == null) {
                        ChatUtil.sendMessage(player, MessagesManager.commandErrorMessage);
                        return;
                    }
                    if(!user.hasGuild()) {
                        ChatUtil.sendMessage(player, MessagesManager.error("Aby podbic inna gildie musisz posiadac wlasna!"));
                        return;
                    }
                    Guild userGuild = user.getGuild();
                    if(userGuild.equals(guild)) {
                        ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz podbic swojej gildii!"));
                        return;
                    }
                    if(this.plugin.getAllianceManager().hasAlliance(guild, userGuild)) {
                        ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz podbic gildii sojusznika!"));
                        return;
                    }
                    if(!guild.getRegion().isInCenter(player.getLocation().getBlock().getLocation())) {
                        ChatUtil.sendMessage(player, MessagesManager.error("Musisz stac blizej centrum aby podbic gildie!"));
                        return;
                    }
                    if(guild.getLastAttackTime() > System.currentTimeMillis()){
                        ChatUtil.sendMessage(player, MessagesManager.error("Gildia moze zostac zaatakowana za "+TimeUtil.getTimeToString(guild.getLastAttackTime() - System.currentTimeMillis())));
                        return;
                    }
                    if(guild.getHealth() <= 1) {
                        this.plugin.getGuildManager().deleteGuild(guild);
                        if(userGuild.getHealth() < ConfigManager.guildStartHealth) {
                            userGuild.setHealth(userGuild.getHealth() + 1);
                        }
                        player.playSound(block.getLocation(), Sound.ENDERDRAGON_DEATH, 20.0F, 20.0F);
                        String message = MessagesManager.guildDestroyMessage;
                        message = message.replace("{TAG1}", guild.getTag());
                        message = message.replace("{TAG2}", userGuild.getTag());
                        String finalMessage = message;
                        Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
                        return;
                    }
                    guild.setHealth(guild.getHealth() - 1);
                    guild.setLastAttackTime(System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.guildNextAttackAfterAttack));
                    if(userGuild.getHealth() < ConfigManager.guildStartHealth) {
                        userGuild.setHealth(userGuild.getHealth() + 1);
                    }
                    player.playSound(block.getLocation(), Sound.ENDERDRAGON_DEATH, 20.0F, 20.0F);
                    String message = MessagesManager.guildAttackMessage;
                    message = message.replace("{TAG2}", guild.getTag());
                    message = message.replace("{TAG1}", userGuild.getTag());
                    String finalMessage = message;
                    Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, finalMessage));
                });
                return;
            }
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                this.plugin.getGuildManager().getGuildByLocation(block.getLocation()).peek(guild -> {
                    if(!guild.getRegion().isInCenter(block.getLocation())) {
                        return;
                    }
                    User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
                    if(user == null) {
                        ChatUtil.sendMessage(player, MessagesManager.commandErrorMessage);
                        return;
                    }
                    if(!user.hasGuild()) {
                        this.plugin.getGuildManager().getGuildInfoMessages(guild).forEach(s -> {
                            ChatUtil.sendMessage(player, s);
                        });
                        return;
                    }
                    Guild userGuild = user.getGuild();
                    if(userGuild.equals(guild)) {
                        this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
                        return;
                    }
                    this.plugin.getGuildManager().getGuildInfoMessages(guild).forEach(s -> {
                        ChatUtil.sendMessage(player, s);
                    });
                    return;
                });
                return;
            }
            return;
        }
    }
}
