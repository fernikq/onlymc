package pl.fernikq.core.listener.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CoreAPI;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.listener.custom.BlockDigEvent;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.PacketUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

public class PlayerJoinListener implements Listener {

    private final CorePlugin plugin;

    public PlayerJoinListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.isOnline() && player.isDead()) {
            player.spigot().respawn();
        }
        event.setJoinMessage(ChatUtil.fixColor(MessagesManager.playerJoinMessage.replace("{PLAYER}", player.getName())));
        User user = this.plugin.getUserManager().getUser(player);
        user.setLogout(false);
        user.getUserStat().setJoinTime(System.currentTimeMillis());
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.plugin.getUserPermissionsManager().reloadPermissions(user);
        }, 60);
        user.setLastAddress(player.getAddress().getAddress().getHostAddress());
        if(user.getScoreboard() == null) {
            user.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        player.setScoreboard(user.getScoreboard());
        this.plugin.getDummyManager().createScore(user);
        this.plugin.getDummyManager().updateScore(user);
        this.plugin.getTagManager().createTag(player);
        if(user.getSidebar().isEnabled()) {
            user.getSidebar().create();
        }
        for(Player vanished : this.plugin.getVanishManager().getVanished()) {
            if(user.canByGroup(UserGroup.HELPER)) {
                break;
            }
            player.hidePlayer(vanished);
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if(day != user.getUserStat().getComebackDay()) {
            if(day - user.getUserStat().getComebackDay() == 1) {
                user.getUserStat().setComebackDaysInRow(user.getUserStat().getComebackDaysInRow() + 1);
                user.getUserStat().setComebackDay(day);
                this.plugin.getQuestManager().checkQuest(user, QuestType.COMEBACK);
            } else {
                user.getUserStat().setComebackDay(day);
                user.getUserStat().setComebackDaysInRow(1);
            }
        }
        if(user.getIncognito().isHideOriginalSkin()) {
            this.plugin.getIncognitoManager().changeSkin(user, false);
        }
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    CoreAPI.getPlugin().getServer().getScheduler().runTask(CoreAPI.getPlugin(), () -> {
                        BlockPosition blockPosition = (BlockPosition) PacketUtil.getField(packet, "a");
                        Location location = new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                        BlockDigEvent blockDigEvent = new BlockDigEvent(player, player.getWorld().getBlockAt(location));
                        Bukkit.getPluginManager().callEvent(blockDigEvent);
                    });
                }
                super.channelRead(channelHandlerContext, packet);
            }
            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                super.write(channelHandlerContext, packet, channelPromise);
            }
        };
        ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
}

