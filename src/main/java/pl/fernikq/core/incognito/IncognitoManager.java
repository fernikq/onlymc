package pl.fernikq.core.incognito;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.util.Reflection;

public class IncognitoManager {

    private final CorePlugin plugin;

    public IncognitoManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public void setSkin(Player player){
        String skin = "eyJ0aW1lc3RhbXAiOjE1MjY4MDczNDc1MjgsInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19";
        String signature = "fzBEi4s/C8nj18tVHIWBlWQEObm65IfATc4P83iB3PEHE44M0wGQjpyT+ZQGXcbgNeVFQ0FPyEwA8/SC3iGrZ5eKy8s5LCfI5LvOPWYQLHPlwEmRgix9dhpf+vKVrirUEkzDuY/oM3cRfLBbNk1afl+df36oixeG4cqsbLEnSJRu/kOMtA5Fcic2NQf7g402pNeqD2D8cq4Hbe47g2UcfIRVsGt0fLif2qsojbha5m6dYYUEfJOmNcGqPiubznxgGS3vpQ8GHRZrntMJbmywrDAOZjgxNmi+Bdq476nJ84NZycBe3BqgtmKFp+WF6z6jxPeQ1ZcUnlEzmsRJwhfS7zHb4Ujyvzn5BxzMegTmsP33cplCydcd/2oXhKnMj4xtmQtrHS10aUs4oa2M7Ak60SVm11qAOR1KwGvMcDY37shvzjK/4cwuspfsgSBIlVC6MJGBgqmc571LWixSJYBRl2HvW/ao43XbN8k9/oegh7SBJMusdO3ADtbOmt84GmzoEbLfWTi4uEkJpYkPfK4UiqvTnB0Uw+KyRJCdoRwpDNRVMZFTb/eJO4Cr2tAIVTM1JR1E5hWaQ7IQBH+Bwj39JjBpK7MLpx0jjZV+y09+u3BrUIVgrLYFQP0WZxypw45+SAuk/P35hG10ERGjwYRZ6PMWnevq13fYUHlc0Crbn3I=";
        try {
            Object entityPlayer = Reflection.invokeMethod(player.getClass(), player, "getHandle");
            Object profile = Reflection.invokeMethod(entityPlayer.getClass(), entityPlayer, "getProfile");
            Object properties = Reflection.invokeMethod(profile.getClass(), profile, "getProperties");
            Reflection.invokeMethod(properties, "clear");
            Reflection.invokeMethod(properties.getClass(), properties, "put", new Class[]{Object.class, Object.class}, "textures", new Property("textures", skin, signature));
        }catch(Exception e){
            e.printStackTrace();
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().forEach(o -> {
                    ((CraftPlayer)o).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
                    ((CraftPlayer)o).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(((CraftPlayer)player).getHandle().getId()));
                    o.hidePlayer(player);
                });
            }
        }.runTaskLater(this.plugin, 1);
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().forEach(o -> {
                    ((CraftPlayer)o).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle()));
                    ((CraftPlayer)o).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
                    o.showPlayer(player);
                });
            }
        }.runTaskLater(this.plugin, 2);
    }
}
