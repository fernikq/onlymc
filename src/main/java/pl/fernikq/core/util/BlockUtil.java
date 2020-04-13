package pl.fernikq.core.util;

public class BlockUtil {

    private static Reflection.FieldAccessor<Float> durabilityField = Reflection.getField(Reflection.getMinecraftClass("Block"), "durability", Float.TYPE);

    public static void setDurability(String name, float durability) {
        Reflection.FieldAccessor<Object> f = Reflection.getSimpleField(Reflection.getMinecraftClass("Blocks"), name.toUpperCase());
        durabilityField.set(f.get(null), Float.valueOf(durability));
    }
}
