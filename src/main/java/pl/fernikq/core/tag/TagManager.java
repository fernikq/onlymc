package pl.fernikq.core.tag;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.alliances.RelationType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

public class TagManager {

    private final CorePlugin plugin;
    private Scoreboard scoreboard;

    public TagManager(CorePlugin plugin){
        this.plugin = plugin;
        this.scoreboard = new Scoreboard();
    }

    private String getTagFormat(User user1, User user2){
        RelationType relationType = this.plugin.getAllianceManager().getRelation(user1, user2);
        Guild guild = user1.getGuild();
        if(guild != null){
            if(relationType.equals(RelationType.TEAM)){
                return MessagesManager.playerNametagGuildOwnFormat.replace("{GUILD}", guild.getTag())+"&f";
            }
            if(relationType.equals(RelationType.ALLY)){
                return MessagesManager.playerNametagGuildAllyFormat.replace("{GUILD}", guild.getTag())+"&f";
            }
            return MessagesManager.playerNametagGuildEnemyFormat.replace("{GUILD}", guild.getTag())+"&f";
        }
        return "";
    }

    public void createTag(Player player){
        if(player == null || !player.isOnline()){
            return;
        }
        try{
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                ScoreboardTeam scoreboardTeam = null;
                if(scoreboard.getTeam(user.getName()) == null){
                    scoreboardTeam = scoreboard.createTeam(user.getName());
                }
                scoreboard.addPlayerToTeam(user.getName(), scoreboardTeam.getName());
                scoreboardTeam.setDisplayName("");
                String prefix;
                prefix = getTagFormat(user, user);
                prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8] " : prefix;
                scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                scoreboardTeam.setSuffix(ChatUtil.fixColor(" "+user.getGroup().getTag()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
                for(Player online : Bukkit.getOnlinePlayers()){
                    if(!online.equals(player)){
                        User onlineUser = this.plugin.getUserManager().getUser(online.getUniqueId()).getOrNull();
                        if(onlineUser == null){
                            continue;
                        }
                        prefix = getTagFormat(user, onlineUser);
                        prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                        scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                        ((CraftPlayer)online).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
                        ScoreboardTeam team = scoreboard.getTeam(online.getName());
                        if(team == null){
                            continue;
                        }
                        prefix = getTagFormat(onlineUser, user);
                        prefix = this.plugin.getVanishManager().isVanished(online) ? "&8[&bV&8]&f " : prefix;
                        team.setPrefix(ChatUtil.fixColor(prefix));
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateTag(Player player) {
        if(player == null || !player.isOnline()){
            return;
        }
        try {
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                ScoreboardTeam team = scoreboard.getTeam(user.getName());
                if(team == null) {
                    return;
                }
                team.setDisplayName("");
                team.setSuffix(ChatUtil.fixColor(" "+user.getGroup().getTag()));
                for(Player online : Bukkit.getServer().getOnlinePlayers()) {
                    User onlineUser = this.plugin.getUserManager().getUser(online.getUniqueId()).getOrNull();
                    if(onlineUser == null){
                        continue;
                    }
                    String prefix;
                    prefix = getTagFormat(user, onlineUser);
                    prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                    team.setPrefix(ChatUtil.fixColor(prefix));
                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 2));
                    ScoreboardTeam scoreboardTeam = scoreboard.getTeam(online.getName());
                    if(scoreboardTeam == null){
                        continue;
                    }
                    prefix = getTagFormat(onlineUser, user);
                    prefix = this.plugin.getVanishManager().isVanished(online) ? "&8[&bV&8]&f " : prefix;
                    scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 2));
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerTags(){
        try{
            Bukkit.getOnlinePlayers().forEach(online -> updateTag(online));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void removeTag(Player player) {
        if(player == null || !player.isOnline()){
            return;
        }
        try {
            ScoreboardTeam team = null;
            if(scoreboard.getTeam(player.getName()) == null) {
                return;
            }
            team = scoreboard.getTeam(player.getName());
            scoreboard.removePlayerFromTeam(player.getName(), team);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.equals(player)) {
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                    ScoreboardTeam t = scoreboard.getTeam(online.getName());
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(t, 1));
                }
            }
            scoreboard.removeTeam(team);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
