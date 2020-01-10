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
                if(this.plugin.getIncognitoManager().changeGuildTag(user1, user2)){
                    return MessagesManager.playerNametagGuildEnemyFormat.replace("{GUILD}", "???")+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
                }
                return MessagesManager.playerNametagGuildOwnFormat.replace("{GUILD}", guild.getTag())+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
            }
            if(relationType.equals(RelationType.ALLY)){
                if(this.plugin.getIncognitoManager().changeGuildTag(user1, user2)){
                    return MessagesManager.playerNametagGuildEnemyFormat.replace("{GUILD}", "???")+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
                }
                return MessagesManager.playerNametagGuildAllyFormat.replace("{GUILD}", guild.getTag())+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
            }
            if(this.plugin.getIncognitoManager().changeGuildTag(user1, user2)){
                return MessagesManager.playerNametagGuildEnemyFormat.replace("{GUILD}", "???")+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
            }
            return MessagesManager.playerNametagGuildEnemyFormat.replace("{GUILD}", guild.getTag())+(this.plugin.getIncognitoManager().changeName(user1, user2) ? "&f&k" : "&f");
        }
        return this.plugin.getIncognitoManager().changeName(user1, user2) ? "&k" : "";
    }

    private String getSuffixFormat(User user1, User user2){
        if(this.plugin.getIncognitoManager().changeRank(user1, user2)){
            return "";
        }
        return "&f "+user1.getGroup().getTag();
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
                if(prefix.length() > 16){
                    prefix = "&cTAG>16";
                }
                scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                scoreboardTeam.setSuffix(ChatUtil.fixColor(getSuffixFormat(user, user)));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
                for(Player online : Bukkit.getOnlinePlayers()){
                    if(!online.equals(player)){
                        User onlineUser = this.plugin.getUserManager().getUser(online.getUniqueId()).getOrNull();
                        if(onlineUser == null){
                            continue;
                        }
                        prefix = getTagFormat(user, onlineUser);
                        prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                        if(prefix.length() > 16){
                            prefix = "&cTAG>16";
                        }
                        scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                        String suffix = getSuffixFormat(user, onlineUser);
                        scoreboardTeam.setSuffix(ChatUtil.fixColor(suffix));
                        ((CraftPlayer)online).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
                        ScoreboardTeam team = scoreboard.getTeam(online.getName());
                        if(team == null){
                            continue;
                        }
                        prefix = getTagFormat(onlineUser, user);
                        prefix = this.plugin.getVanishManager().isVanished(online) ? "&8[&bV&8]&f " : prefix;
                        if(prefix.length() > 16){
                            prefix = "&cTAG>16";
                        }
                        team.setPrefix(ChatUtil.fixColor(prefix));
                        suffix = getSuffixFormat(onlineUser, user);
                        team.setSuffix(ChatUtil.fixColor(suffix));
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
                this.plugin.getDummyManager().updateScore(user);
                team.setDisplayName("");
                for(Player online : Bukkit.getServer().getOnlinePlayers()) {
                    User onlineUser = this.plugin.getUserManager().getUser(online.getUniqueId()).getOrNull();
                    if(onlineUser == null){
                        continue;
                    }
                    String prefix;
                    prefix = getTagFormat(user, onlineUser);
                    prefix = this.plugin.getVanishManager().isVanished(player) ? "&8[&bV&8]&f " : prefix;
                    if(prefix.length() > 16){
                        prefix = "&cTAG>16";
                    }
                    team.setPrefix(ChatUtil.fixColor(prefix));
                    String suffix = getSuffixFormat(user, onlineUser);
                    team.setSuffix(ChatUtil.fixColor(suffix));
                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, 2));
                    ScoreboardTeam scoreboardTeam = scoreboard.getTeam(online.getName());
                    if(scoreboardTeam == null){
                        continue;
                    }
                    prefix = getTagFormat(onlineUser, user);
                    prefix = this.plugin.getVanishManager().isVanished(online) ? "&8[&bV&8]&f " : prefix;
                    if(prefix.length() > 16){
                        prefix = "&cTAG>16";
                    }
                    scoreboardTeam.setPrefix(ChatUtil.fixColor(prefix));
                    suffix = getSuffixFormat(onlineUser, user);
                    scoreboardTeam.setSuffix(ChatUtil.fixColor(suffix));
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 2));
                    this.plugin.getDummyManager().updateScore(onlineUser);
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
