package pl.fernikq.core.user.disco;

import org.bukkit.Color;

import java.util.concurrent.ThreadLocalRandom;

public class DiscoArmorColorUtil {

    public static Color nextColor(Color color) {
        return Color.fromRGB(ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255), ThreadLocalRandom.current().nextInt(0, 255));
    }
}
