package pl.fernikq.core.inventory.actions.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.discord.util.DiscordUserUtil;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.RewardActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.nsclient.spigot.MCPlugin;

import java.util.concurrent.TimeUnit;

public class RewardAction implements InventoryAction {

    private User user;
    private CorePlugin plugin;
    private RewardActionType type;

    public RewardAction(CorePlugin plugin, RewardActionType type, User user){
        this.user = user;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if (type.equals(RewardActionType.CLIENT)) {
            if(!MCPlugin.getAuthorizedPlayers().contains(user.getName())){
                ChatUtil.sendMessage(player, MessagesManager.error("Aby to zrobic musisz uzywac paczki NsClient.pl"));
                return;
            }
            if(user.getClientRewardTime() > System.currentTimeMillis()){
                ChatUtil.sendMessage(player, MessagesManager.error("Kolejny raz nagrode mozesz odebrac za "+ TimeUtil.getTimeToString(user.getClientRewardTime() - System.currentTimeMillis())));
                return;
            }
            this.user.setClientRewardTime(System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.clientRewardTime));
            this.user.getUserStat().setTurboDropTime(user.getUserStat().getTurboDropTime() + (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));
            ItemUtil.giveItems(user.asPlayer(), new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone()).setAmount(3).toItemStack());
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.plugin.getUserManager().getUserData().updateClientReward(this.user);
            });
            Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8[&6&lNsClient&8] &8>> &fGracz &6"+user.getName()+" &fodebral nagrode za gre na &6NsClient&f! Ty tez mozesz ja otrzymac, wszystkie informacje znajduja sie stronie &6www.nsclient.pl"));
            return;
        }
        if (type.equals(RewardActionType.DISCORD)) {
            if(!this.user.isDiscordRewardAllowed()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz tego zrobic! Wiecej informacji pod komenda /discord"));
                return;
            }
            if(user.getDiscordRewardTime() > System.currentTimeMillis()){
                ChatUtil.sendMessage(player, MessagesManager.error("Kolejny raz nagrode mozesz odebrac za "+ TimeUtil.getTimeToString(user.getDiscordRewardTime() - System.currentTimeMillis())));
                return;
            }
            this.user.setDiscordRewardAllowed(false);
            this.user.setDiscordRewardTime(System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.discordBotRewardTime));
            this.user.getUserStat().setTurboDropTime(user.getUserStat().getTurboDropTime() + (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));
            ItemUtil.giveItems(user.asPlayer(), new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone()).setAmount(3).toItemStack());
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.plugin.getUserManager().getUserData().updateDiscordReward(this.user);
            });
            Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, "&8[&5&lDISCORD&8] &8>> &fGracz &5"+user.getName()+" &fodebral nagrode za wejscie na &5Discorda&f! Ty tez mozesz ja otrzymac, wszystkie informacje znajduja sie pod komenda &5/discord"));
            return;
        }
    }
}
