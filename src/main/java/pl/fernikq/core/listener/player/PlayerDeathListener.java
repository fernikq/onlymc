package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.magiccase.MagicCaseType;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.backup.Backup;
import pl.fernikq.core.user.backup.BackupBuilder;
import pl.fernikq.core.user.fight.Damage;
import pl.fernikq.core.user.fight.UserFight;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.*;

import java.util.List;
import java.util.Map;

public class PlayerDeathListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDeathListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event){
        event.setDeathMessage(null);
        Player player = event.getEntity();
        if(player.isOnline() && player.isDead()){
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> player.spigot().respawn(), 1);
        }
        User victimUser = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        if(victimUser == null){
            return;
        }
        victimUser.getEnderPearls().forEach(entity -> entity.remove());
        victimUser.getEnderPearls().clear();
        String reason = "Brak";
        if(victimUser.getUserFight().isDuringFight()){
            reason = "Zabity przez "+victimUser.getUserFight().getLastAttacker().getName();
        }else{
            reason = (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null) ? player.getLastDamageCause().getCause().name() : "Brak danych";
        }
        if(victimUser.isLogout()){
            reason = "Logout";
        }
        Backup backup = BackupBuilder.builder().setArmor(player.getInventory().getArmorContents()).setItems(player.getInventory().getContents()).setDeathTime(System.currentTimeMillis())
                .setPoints(victimUser.getUserStat().getPoints()).setDeaths(victimUser.getUserStat().getDeaths()).setUser(victimUser)
                .setPing(PlayerUtil.getPing(player)).setReason(reason).build();
        this.plugin.runAsync(() -> this.plugin.getUserManager().getBackupData().insertBackup(backup));
        victimUser.getUserStat().setDeaths(victimUser.getUserStat().getDeaths() + 1);
        this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_DEATHS).setSorted(false));
        if(victimUser.hasGuild()){
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_DEATHS).setSorted(false));
        }
        UserFight victimFight = victimUser.getUserFight();
        if(victimFight.getDamageMap().isEmpty() && victimFight.getLastAttacker() == null){
            if(victimFight.isDuringFight()){
                this.plugin.getFightManager().removeFight(victimUser);
            }
            return;
        }
        List<Damage> damageList = victimFight.getDamageList();
        User killerUser = victimFight.getLastAttacker();
        Damage killerDamage = victimFight.getDamageByUser(killerUser);
        damageList.remove(killerDamage);
        if(victimUser.getLastAddress().equals(killerUser.getLastAddress())){
            ChatUtil.sendMessage(victimUser.asPlayer(), MessagesManager.error("Podany gracz posiada to samo IP co ty, punkty nie zostaly odebrane!"));
            ChatUtil.sendMessage(killerUser.asPlayer(), MessagesManager.error("Podany gracz posiada to samo IP co ty, punkty nie zostaly przyznane!"));
            if(!damageList.isEmpty()) {
                Damage assistDamage = damageList.get(0);
                User assistUser = assistDamage.getUser();
                if(assistUser.getLastAddress().equals(victimUser.getLastAddress())){
                    this.plugin.getFightManager().removeFight(victimUser);
                    return;
                }
                int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
                int maxAssistPoints = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), assistUser.getUserStat().getPoints());
                int assistPoints = points * 25/100;
                if(assistPoints > maxAssistPoints){
                    assistPoints = maxAssistPoints;
                }
                if(assistPoints < 0){
                    assistPoints = 0;
                }
                ChatUtil.sendMessage(assistUser.asPlayer(), "&8>> {n}Asystowales w zabojstwie gracza {c}"+(this.plugin.getIncognitoManager().changeName(victimUser, assistUser) ? "&k"+victimUser.getName() : victimUser.getName())+" {n} i otrzymujesz &a"+assistPoints+" {n}punktow");
                assistUser.getUserStat().setPoints(assistUser.getUserStat().getPoints() + assistPoints);
                assistUser.getUserStat().setAssists(assistUser.getUserStat().getAssists() + 1);
                victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - assistPoints);
                ChatUtil.sendMessage(victimUser.asPlayer(), "&8>> {n}Gracz {c}"+(this.plugin.getIncognitoManager().changeName(assistUser, victimUser) ? "&k"+assistUser.getName() : assistUser.getName())+" {n}asystowal przy twoim zabojstwie i przez to tracisz &c"+assistPoints+" {n}punktow");
                this.plugin.getQuestManager().checkQuest(assistUser, QuestType.ASSISTS);
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_ASSISTS).setSorted(false));
                if(assistUser.hasGuild()){
                    this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_ASSISTS).setSorted(false));
                }
            }
            this.plugin.getFightManager().removeFight(victimUser);
            return;
        }
        if(killerUser.getUserFight().wasKilledLastTime(victimUser)){
            ChatUtil.sendMessage(victimUser.asPlayer(), MessagesManager.error("Podany gracz zabil cie ostatnio, punkty nie zostaly odebrane!"));
            ChatUtil.sendMessage(killerUser.asPlayer(), MessagesManager.error("Podany gracz zostal przez ciebie ostatnio zabity, punkty nie zostaly przyznane!"));
            if(!damageList.isEmpty()) {
                Damage assistDamage = damageList.get(0);
                User assistUser = assistDamage.getUser();
                int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
                int maxAssistPoints = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), assistUser.getUserStat().getPoints());
                int assistPoints = points * 25/100;
                if(assistPoints > maxAssistPoints){
                    assistPoints = maxAssistPoints;
                }
                if(assistPoints < 0){
                    assistPoints = 0;
                }
                ChatUtil.sendMessage(assistUser.asPlayer(), "&8>> {n}Asystowales w zabojstwie gracza {c}"+(this.plugin.getIncognitoManager().changeName(victimUser, assistUser) ? "&k"+victimUser.getName() : victimUser.getName())+" {n} i otrzymujesz &a"+assistPoints+" {n}punktow");
                assistUser.getUserStat().setPoints(assistUser.getUserStat().getPoints() + assistPoints);
                assistUser.getUserStat().setAssists(assistUser.getUserStat().getAssists() + 1);
                victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - assistPoints);
                ChatUtil.sendMessage(victimUser.asPlayer(), "&8>> {n}Gracz {c}"+(this.plugin.getIncognitoManager().changeName(assistUser, victimUser) ? "&k"+assistUser.getName() : assistUser.getName())+" {n}asystowal przy twoim zabojstwie i przez to tracisz &c"+assistPoints+" {n}punktow");
                this.plugin.getQuestManager().checkQuest(assistUser, QuestType.ASSISTS);
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_ASSISTS).setSorted(false));
                if(assistUser.hasGuild()){
                    this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_ASSISTS).setSorted(false));
                }
            }
            this.plugin.getFightManager().removeFight(victimUser);
            return;
        }
        if(!damageList.isEmpty()){
            Damage assistDamage = damageList.get(0);
            User assistUser = assistDamage.getUser();
            int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
            int maxAssistPoints = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), assistUser.getUserStat().getPoints());
            int assistPoints = points * 25/100;
            if(assistPoints > maxAssistPoints){
                assistPoints = maxAssistPoints;
            }
            points = points - assistPoints;
            if(points < 0){
                points = 0;
            }
            if(assistPoints < 0){
                assistPoints = 0;
            }
            killerUser.getUserStat().setPoints(killerUser.getUserStat().getPoints() + points);
            assistUser.getUserStat().setPoints(assistUser.getUserStat().getPoints() + assistPoints);
            assistUser.getUserStat().setAssists(assistUser.getUserStat().getAssists() + 1);
            this.plugin.getQuestManager().checkQuest(assistUser, QuestType.ASSISTS);
            victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - points);
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.USER_ASSISTS).setSorted(false));
            if(assistUser.hasGuild()){
                this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_ASSISTS).setSorted(false));
            }
            this.plugin.getDummyManager().updateScore(assistUser);
            this.plugin.getDummyManager().updateScore(victimUser);
            this.plugin.getDummyManager().updateScore(killerUser);
            int finalPoints = points;
            int finalAssistPoints = assistPoints;
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isFightMessages()).forEach(onlineUser -> {
                String message = MessagesManager.playerFightMessage;
                message = message.replace("{KILLER}", this.plugin.getIncognitoManager().changeName(killerUser, onlineUser) ? "&k"+killerUser.getName() : killerUser.getName());
                message = message.replace("{KILLER-GUILD}", killerUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", this.plugin.getIncognitoManager().changeGuildTag(killerUser, onlineUser) ? "???" : killerUser.getGuild().getTag()) : "");
                message = message.replace("{KILLER-POINTS}", Integer.toString(finalPoints));
                message = message.replace("{VICTIM}", this.plugin.getIncognitoManager().changeName(victimUser, onlineUser) ? "&k"+victimUser.getName() : victimUser.getName());
                message = message.replace("{VICTIM-GUILD}", victimUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", this.plugin.getIncognitoManager().changeGuildTag(victimUser, onlineUser) ? "???" : victimUser.getGuild().getTag()) : "");
                message = message.replace("{VICTIM-POINTS}", Integer.toString(finalPoints));
                String assistMessage = MessagesManager.playerFightPlusAssistMessage;
                assistMessage = assistMessage.replace("{ASSIST}", this.plugin.getIncognitoManager().changeName(assistUser, onlineUser) ? "&k"+assistUser.getName() : assistUser.getName());
                assistMessage = assistMessage.replace("{ASSIST-GUILD}", assistUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", this.plugin.getIncognitoManager().changeGuildTag(assistUser, onlineUser) ? "???" : assistUser.getGuild().getTag()) : "");
                assistMessage = assistMessage.replace("{ASSIST-POINTS}", Integer.toString(finalAssistPoints));
                ChatUtil.sendMessage(onlineUser.asPlayer(), message);
                ChatUtil.sendMessage(onlineUser.asPlayer(), assistMessage);
            });
        }else{
            int points = RankingUtil.calculatePoints(victimUser.getUserStat().getPoints(), killerUser.getUserStat().getPoints());
            killerUser.getUserStat().setPoints(killerUser.getUserStat().getPoints() + points);
            victimUser.getUserStat().setPoints(victimUser.getUserStat().getPoints() - points);
            this.plugin.getDummyManager().updateScore(victimUser);
            this.plugin.getDummyManager().updateScore(killerUser);
            this.plugin.getUserManager().getOnlineUsers().stream().filter(onlineUser -> onlineUser.getUserChat().isFightMessages()).forEach(onlineUser -> {
                String message = MessagesManager.playerFightMessage;
                message = message.replace("{KILLER}", this.plugin.getIncognitoManager().changeName(killerUser, onlineUser) ? "&k"+killerUser.getName() : killerUser.getName());
                message = message.replace("{KILLER-GUILD}", killerUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", this.plugin.getIncognitoManager().changeGuildTag(killerUser, onlineUser) ? "???" : killerUser.getGuild().getTag()) : "");
                message = message.replace("{KILLER-POINTS}", Integer.toString(points));
                message = message.replace("{VICTIM}", this.plugin.getIncognitoManager().changeName(victimUser, onlineUser) ? "&k"+victimUser.getName() : victimUser.getName());
                message = message.replace("{VICTIM-GUILD}", victimUser.hasGuild() ? MessagesManager.playerChatGuildFormat.replace("{GUILD}", this.plugin.getIncognitoManager().changeGuildTag(victimUser, onlineUser) ? "???" : victimUser.getGuild().getTag()) : "");
                message = message.replace("{VICTIM-POINTS}", Integer.toString(points));
                ChatUtil.sendMessage(onlineUser.asPlayer(), message);
            });
        }
        killerUser.getUserFight().setKilledLastTime(victimUser);
        if(victimUser.canByGroup(UserGroup.VIP)){
            killerUser.getUserStat().getKilledWithRankUsers().add(victimUser);
            this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_USERS_WITH_RANK);
        }
        killerUser.getUserStat().getKilledUsers().add(victimUser);
        killerUser.getUserStat().setKills(killerUser.getUserStat().getKills() + 1);
        this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_USER);
        this.plugin.getQuestManager().checkQuest(killerUser, QuestType.KILL_UNIQUE_USERS);
        for(Map.Entry map : this.plugin.getMagicCaseManager().getKillingChance().entrySet()){
            MagicCaseType magicCaseType = (MagicCaseType)map.getKey();
            if(RandomUtil.getChance((double)map.getValue())){
                killerUser.getUserStat().addKeyFragmentsByMagicCaseType(magicCaseType, 1);
                ChatUtil.sendMessage(killerUser.asPlayer(), "&8>> &fOtrzymales fragment klucza do skrzyni o typie&8: "+magicCaseType.getName());
            }
        }
        this.plugin.runAsync(() -> {
            this.plugin.getTopManager().getTopByType(TopType.USER_POINTS).setSorted(false);
            this.plugin.getTopManager().getTopByType(TopType.USER_KILLS).setSorted(false);
        });
        if(killerUser.hasGuild() || victimUser.hasGuild()){
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_POINTS).setSorted(false));
            this.plugin.runAsync(() -> this.plugin.getTopManager().getTopByType(TopType.GUILD_KILLS).setSorted(false));
        }
        this.plugin.getFightManager().removeFight(victimUser);
    }
}
