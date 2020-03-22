package pl.fernikq.core.discord.util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class DiscordUserUtil {

    public static void sendPrivateMessage(User user, String message, String errorMessage, MessageChannel channel) {
        user.openPrivateChannel().complete().sendMessage(message.substring(0, Math.min(message.length(), 1999))).queue(message1 -> {}, throwable -> channel.sendMessage(errorMessage.substring(0, Math.min(errorMessage.length(), 1999))).queue());
    }

    public static void sendPrivateMessage(User user, String message) {
        user.openPrivateChannel().complete().sendMessage(message.substring(0, Math.min(message.length(), 1999))).queue(message1 -> {}, throwable -> {});
    }
}
