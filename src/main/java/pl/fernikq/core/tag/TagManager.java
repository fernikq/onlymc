package pl.fernikq.core.tag;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class TagManager {

    private final CorePlugin plugin;
    private Scoreboard scoreboard;

    public TagManager(CorePlugin plugin){
        this.plugin = plugin;
        this.scoreboard = new Scoreboard();
    }

    public void createTag(Player player){
        if(player == null){
            return;
        }
        try{
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                ScoreboardTeam scoreboardTeam = null;
                if(scoreboard.getPlayerTeam(user.getName()) == null){
                    scoreboardTeam = scoreboard.createTeam(user.getName());
                }
                scoreboard.addPlayerToTeam(user.getName(), scoreboardTeam.getName());
                scoreboardTeam.setDisplayName("");
                String prefix = "";
                prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8] " : prefix;
                scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                scoreboardTeam.setSuffix(ChatUtil.fixColor(" "+user.getGroup().getTag()));
                PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(scoreboardTeam, 0);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                for(Player online : Bukkit.getOnlinePlayers()){
                    if(!online.equals(player)){
                        prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                        scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                        ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
                        ScoreboardTeam team = scoreboard.getTeam(online.getName());
                        PacketPlayOutScoreboardTeam packetShow = new PacketPlayOutScoreboardTeam(team, 0);
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetShow);
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateTag(Player player) {
        if(player == null){
            return;
        }
        try {
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                ScoreboardTeam team = scoreboard.getPlayerTeam(user.getName());
                if(team == null) {
                    return;
                }
                team.setDisplayName("");
                team.setSuffix(ChatUtil.fixColor(" "+user.getGroup().getTag()));
                for(Player online : Bukkit.getServer().getOnlinePlayers()) {
                    String prefix = "";
                    prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                    team.setPrefix(ChatUtil.fixColor(prefix));
                    PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(team, 2);
                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTag(Player player) {
        if(player == null){
            return;
        }
        try {
            ScoreboardTeam team = null;
            if(scoreboard.getPlayerTeam(player.getName()) == null) {
                return;
            }
            team = scoreboard.getPlayerTeam(player.getName());
            scoreboard.removePlayerFromTeam(player.getName(), team);
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(team, 1);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.equals(player)) {
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
                    ScoreboardTeam t = scoreboard.getTeam(online.getName());
                    PacketPlayOutScoreboardTeam packetHide = new PacketPlayOutScoreboardTeam(t, 1);
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetHide);
                }
            }
            scoreboard.removeTeam(team);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
