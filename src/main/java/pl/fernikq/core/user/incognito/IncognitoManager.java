package pl.fernikq.core.user.incognito;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;

import java.util.Collection;

public class IncognitoManager {

    private final CorePlugin plugin;

    public IncognitoManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public boolean changeName(User user, User target){
        if(user.equals(target)){
            return false;
        }
        UserIncognito incognito = user.getIncognito();
        if(incognito.getShowNickName().equals(IncognitoType.ALL)){
            return false;
        }
        Guild guild = user.getGuild();
        Guild targetGuild = target.getGuild();
        if(targetGuild != null && guild != null) {
            if(incognito.getShowNickName().equals(IncognitoType.GUILD) && targetGuild.equals(guild)) {
                return false;
            }
            if(incognito.getShowNickName().equals(IncognitoType.ALLIES) && (targetGuild.equals(guild) || this.plugin.getAllianceManager().hasAlliance(guild, targetGuild))) {
                return false;
            }
        }
        return true;
    }

    public boolean changeGuildTag(User user, User target){
        if(user.equals(target)){
            return false;
        }
        UserIncognito incognito = user.getIncognito();
        if(incognito.getShowGuildTag().equals(IncognitoType.ALL)){
            return false;
        }
        Guild guild = user.getGuild();
        Guild targetGuild = target.getGuild();
        if(targetGuild != null && guild != null) {
            if(incognito.getShowGuildTag().equals(IncognitoType.GUILD) && targetGuild.equals(guild)) {
                return false;
            }
            if(incognito.getShowGuildTag().equals(IncognitoType.ALLIES) && (targetGuild.equals(guild) || this.plugin.getAllianceManager().hasAlliance(guild, targetGuild))) {
                return false;
            }
        }
        return true;
    }

    public boolean changePoints(User user, User target){
        if(user.equals(target)){
            return false;
        }
        UserIncognito incognito = user.getIncognito();
        if(incognito.getShowPoints().equals(IncognitoType.ALL)){
            return false;
        }
        Guild guild = user.getGuild();
        Guild targetGuild = target.getGuild();
        if(targetGuild != null && guild != null) {
            if(incognito.getShowPoints().equals(IncognitoType.GUILD) && targetGuild.equals(guild)) {
                return false;
            }
            if(incognito.getShowPoints().equals(IncognitoType.ALLIES) && (targetGuild.equals(guild) || this.plugin.getAllianceManager().hasAlliance(guild, targetGuild))) {
                return false;
            }
        }
        return true;
    }

    public boolean changeRank(User user, User target){
        if(user.equals(target)){
            return false;
        }
        UserIncognito incognito = user.getIncognito();
        if(incognito.getShowRank().equals(IncognitoType.ALL)){
            return false;
        }
        Guild guild = user.getGuild();
        Guild targetGuild = target.getGuild();
        if(targetGuild != null && guild != null) {
            if(incognito.getShowRank().equals(IncognitoType.GUILD) && targetGuild.equals(guild)) {
                return false;
            }
            if(incognito.getShowRank().equals(IncognitoType.ALLIES) && (targetGuild.equals(guild) || this.plugin.getAllianceManager().hasAlliance(guild, targetGuild))) {
                return false;
            }
        }
        return true;
    }

    public void changeSkin(User user, boolean restore){
        Player player = user.asPlayer();
        if(player == null || !player.isOnline()){
            return;
        }
        UserIncognito incognito = user.getIncognito();
        if(!incognito.hasSkin()){
            return;
        }
        String originalSkin = incognito.getOriginalSkin();
        GameProfile gameProfile = ((CraftPlayer)player).getProfile();
        if(restore){
            String[] skingInfo = originalSkin.split(";");
            gameProfile.getProperties().get("textures").clear();
            gameProfile.getProperties().put("textures", new Property("textures", skingInfo[0], skingInfo[1]));
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(o -> o.hidePlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)player).getHandle().getId()));
            }, 1);
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
            }, 2);
            incognito.setHideOriginalSkin(false);
            return;
        }else{
            if(originalSkin == null){
                Property property = gameProfile.getProperties().get("textures").iterator().next();
                originalSkin = property.getValue()+";"+property.getSignature();
                incognito.setOriginalSkin(originalSkin);
            }
            String skin = "eyJ0aW1lc3RhbXAiOjE1NDg0Mzg3NDU3MDQsInByb2ZpbGVJZCI6IjY0MGE1MzcyNzgwYjRjMmFiN2U3ODM1OWQyZjlhNmE4IiwicHJvZmlsZU5hbWUiOiJBbm9ueW1vdXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YxYzQ3NjQzZGQ0Zjk1OTVhZjJmOWJmNTgzYTdmMjY4M2ZhNTM3YTcyMDJjODNlM2NmNjQzOTE2MDc4OWY3NWEifX19";
            String signature = "R5kTg7wD99FyI6NusT2hSbFtY4kn+TvRkCZKhwJJM0hRCntXx3bJYbujwPU1rGH22Dsw0C3IsV+g+WnJ1VqyqRR7jYDHB4O6KZbgSUOFaZTKqGdzQJRCAluMxEpaSN1yaEyYAQ4INwr9l0ixQTwo/O7ysf++7XiPD/vJTzPD52GcEINwnMbw3p/RJTl3jla5OBSOpePD7OOSGL8UkcpPdPiU8KrLO4OTFliI0vzwWa5GrFEBCPqPg2tTWzt8GDkaSYvGY64W8EWhrJFJAAXxs359o/+KP4/Tn7SgtUP22TFznAwWpXlyzVMN13iqBv+fuPbMUeTUPW1QOHholsuLLHm4cUf38dfPOpNbKW3Dg+R9+Thqy6KXr6B5kR3suAXd58nBmASWJKvZp7JQIiIhHki6zVRDVreQQEQvnTsV4EKSjH1Cw9iT3XdkO9YV7MqEwpXXFky01GFt0/8w39E/0Mf9J6S9lkumeK1cldA7CAv4iaoxSCPswc8glNgxOC4YAfBoOK/0A1uAUQQAxla/AaJJ6RVrkkny42d6DBFGsDwGS5YeCfDRMUKMQPePsGWB2soDY/orXuXH05xZwu7FvjY8TTOllrvgjB1pnfwi4AdZJ6+IuLz0uz6DhAQhKx7tHfw9x38cB+krOSP4eecARX7LXwPHCrtBEUe73aIhox0=";
            gameProfile.getProperties().get("textures").clear();
            gameProfile.getProperties().put("textures", new Property("textures", skin, signature));
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(o -> o.hidePlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)player).getHandle().getId()));
            }, 1);
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
            }, 2);
            incognito.setHideOriginalSkin(true);
            return;
        }
    }
}
