package pl.fernikq.core.discord.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import pl.fernikq.core.CoreAPI;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.discord.util.DiscordUserUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.concurrent.TimeUnit;

public class RewardListener extends ListenerAdapter {

    private final CorePlugin plugin;
    private Cache<User, Long> userLongCache;

    public RewardListener(CorePlugin plugin){
        this.plugin = plugin;
        this.userLongCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        User discordUser = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String botPrefix = ConfigManager.discordBotMessagePrefix;
        if(!channel.getId().equalsIgnoreCase(ConfigManager.discordBotChannelID)){
            return;
        }
        if(discordUser.isBot()){
            return;
        }
        message.delete().queue();
        if(args.length < 1){
            return;
        }
        if(args[0].equalsIgnoreCase(botPrefix+"nagroda")){
            if(args.length < 2){
                DiscordUserUtil.sendPrivateMessage(discordUser, "Aby odebrac nagrode wpisz "+botPrefix+"nagroda <nick>");
                return;
            }
            String minecraftName = args[1];
            pl.fernikq.core.user.User user = this.plugin.getUserManager().getUser(minecraftName).getOrNull();
            if(this.userLongCache.asMap().containsKey(discordUser)){
                DiscordUserUtil.sendPrivateMessage(discordUser, "Nie pisz do mnie tak czesto! Nastepny raz mozesz napisac za "+ TimeUtil.getTimeToString(this.userLongCache.asMap().get(discordUser) - System.currentTimeMillis()));
                return;
            }
            this.userLongCache.put(discordUser, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
            if(user == null){
                DiscordUserUtil.sendPrivateMessage(discordUser, "Aby odebrac nagrode musisz chociaz raz wejsc na serwer :D");
                return;
            }
            if(user.getDiscordRewardTime() > System.currentTimeMillis()){
                DiscordUserUtil.sendPrivateMessage(discordUser, "Kolejny raz nagrode mozesz odebrac za "+ TimeUtil.getTimeToString(user.getDiscordRewardTime() - System.currentTimeMillis()));
                return;
            }
            if(user.asPlayer() == null){
                DiscordUserUtil.sendPrivateMessage(discordUser, "Aby odebrac nagrode musisz byc na serwerze, chyba nie chcesz jej stracic?");
                return;
            }
            user.setDiscordRewardTime(System.currentTimeMillis() + TimeUtil.getTime(ConfigManager.discordBotRewardTime));
            user.getUserStat().setTurboDropTime(user.getUserStat().getTurboDropTime() + (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));
            ItemUtil.giveItems(user.asPlayer(), new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone()).setAmount(3).toItemStack());
            DiscordUserUtil.sendPrivateMessage(discordUser, "Gratulacje! Otrzymales swoja nagrode, zyczymy milej gry :D");
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.plugin.getUserManager().getUserData().updateDiscordRewardTime(user);
            });
            return;
        }
    }
}
