package pl.fernikq.core.util;

import java.lang.reflect.Field;

public class PacketUtil {

    public static Object getField(Object packet, String string) {
        try {
            Field field = packet.getClass().getDeclaredField(string);
            field.setAccessible(true);
            return field.get(packet);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
