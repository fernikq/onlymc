package pl.fernikq.core.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendTitle(Player player, String text, int time){
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\",color:" + ChatColor.WHITE.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(10, time * 20, 5);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(length);
    }

    public static void sendSubTitle(Player player, String text, int time){
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\",color:" + ChatColor.WHITE.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(10, time * 20, 5);

        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(length);
    }

    public static void sendActionBar(Player player, String text){
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);
    }
}
