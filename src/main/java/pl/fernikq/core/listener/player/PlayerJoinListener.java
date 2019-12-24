package pl.fernikq.core.listener.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

public class PlayerJoinListener implements Listener {

    private final CorePlugin plugin;

    public PlayerJoinListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.isOnline() && player.isDead()){
            player.spigot().respawn();
        }
        event.setJoinMessage(ChatUtil.fixColor(MessagesManager.playerJoinMessage.replace("{PLAYER}", player.getName())));
        User user = this.plugin.getUserManager().getUser(player);
        user.getUserStat().setJoinTime(System.currentTimeMillis());
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.plugin.getUserPermissionsManager().reloadPermissions(user);
        },60);
        user.setLastAddress(player.getAddress().getAddress().getHostAddress());
        if(!user.getName().equals(player.getName())){
            user.setName(player.getName());
            this.plugin.getUserManager().updateUserInfo(user);
        }
        if(user.getScoreboard() == null){
            user.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        player.setScoreboard(user.getScoreboard());
        this.plugin.getDummyManager().createScore(user);
        this.plugin.getDummyManager().updateScore(user);
        this.plugin.getTagManager().createTag(player);
        for(Player vanished : this.plugin.getVanishManager().getVanished()){
            if(user.canByGroup(UserGroup.HELPER)){
                break;
            }
            player.hidePlayer(vanished);
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if(day != user.getUserStat().getComebackDay()){
            if(day - user.getUserStat().getComebackDay() == 1){
                user.getUserStat().setComebackDaysInRow(user.getUserStat().getComebackDaysInRow() + 1);
                user.getUserStat().setComebackDay(day);
                this.plugin.runAsync(() -> this.plugin.getQuestManager().checkComebackQuest(user));
            }else{
                user.getUserStat().setComebackDay(day);
                user.getUserStat().setComebackDaysInRow(1);
            }
        }
    }
}

