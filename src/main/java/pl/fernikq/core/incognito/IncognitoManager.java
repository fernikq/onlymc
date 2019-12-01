package pl.fernikq.core.incognito;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.handler.codec.base64.Base64Decoder;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.Reflection;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IncognitoManager {

    private final CorePlugin plugin;
    private Map<Player, String> players;

    public IncognitoManager(CorePlugin plugin){
        this.plugin = plugin;
        this.players = new HashMap<>();
    }

    public void changeSkin(Player player){
        if(player == null || !player.isOnline()){
            return;
        }
        //TODO pobieranie usera, incognitoObiekt, pobieranie czy ma skina on/off ... dzialanie, skin i signature zapisuj w obiekcie incognito, prznies all do obiektu incognito
        if(this.players.containsKey(player)){
            GameProfile profile = ((CraftPlayer)player).getProfile();
            profile.getProperties().get("textures").clear();
            String[] skinInfo = this.players.get(player).split(";;");
            profile.getProperties().put("textures", new Property("textures", skinInfo[0], skinInfo[1]));
            this.players.remove(player);
        }else{
            String skin = "eyJ0aW1lc3RhbXAiOjE1NDg0Mzg3NDU3MDQsInByb2ZpbGVJZCI6IjY0MGE1MzcyNzgwYjRjMmFiN2U3ODM1OWQyZjlhNmE4IiwicHJvZmlsZU5hbWUiOiJBbm9ueW1vdXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YxYzQ3NjQzZGQ0Zjk1OTVhZjJmOWJmNTgzYTdmMjY4M2ZhNTM3YTcyMDJjODNlM2NmNjQzOTE2MDc4OWY3NWEifX19";
            String signature = "R5kTg7wD99FyI6NusT2hSbFtY4kn+TvRkCZKhwJJM0hRCntXx3bJYbujwPU1rGH22Dsw0C3IsV+g+WnJ1VqyqRR7jYDHB4O6KZbgSUOFaZTKqGdzQJRCAluMxEpaSN1yaEyYAQ4INwr9l0ixQTwo/O7ysf++7XiPD/vJTzPD52GcEINwnMbw3p/RJTl3jla5OBSOpePD7OOSGL8UkcpPdPiU8KrLO4OTFliI0vzwWa5GrFEBCPqPg2tTWzt8GDkaSYvGY64W8EWhrJFJAAXxs359o/+KP4/Tn7SgtUP22TFznAwWpXlyzVMN13iqBv+fuPbMUeTUPW1QOHholsuLLHm4cUf38dfPOpNbKW3Dg+R9+Thqy6KXr6B5kR3suAXd58nBmASWJKvZp7JQIiIhHki6zVRDVreQQEQvnTsV4EKSjH1Cw9iT3XdkO9YV7MqEwpXXFky01GFt0/8w39E/0Mf9J6S9lkumeK1cldA7CAv4iaoxSCPswc8glNgxOC4YAfBoOK/0A1uAUQQAxla/AaJJ6RVrkkny42d6DBFGsDwGS5YeCfDRMUKMQPePsGWB2soDY/orXuXH05xZwu7FvjY8TTOllrvgjB1pnfwi4AdZJ6+IuLz0uz6DhAQhKx7tHfw9x38cB+krOSP4eecARX7LXwPHCrtBEUe73aIhox0=";
            GameProfile gameProfile = ((CraftPlayer)player).getProfile();
            if(gameProfile.getProperties().get("textures").isEmpty()){
                return;
            }
            Collection<Property> properties = gameProfile.getProperties().get("textures");
            Property property = properties.iterator().next();
            this.players.put(player, property.getValue()+";;"+property.getSignature());
            gameProfile.getProperties().get("textures").clear();
            gameProfile.getProperties().put("textures", new Property("textures", skin, signature));
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().forEach(o -> o.hidePlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)player).getHandle().getId()));
            }
        }.runTaskLater(this.plugin, 1);
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().forEach(o -> o.showPlayer(player));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle()));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
            }
        }.runTaskLater(this.plugin, 2);
    }

    public void removePlayer(Player player){
        this.players.remove(player);
    }
}
