package pl.fernikq.core.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.List;

public class BlockUtil {

    private static Reflection.FieldAccessor<Float> durabilityField = Reflection.getField(Reflection.getMinecraftClass("Block"), "durability", Float.TYPE);

    public static void setDurability(String name, float durability) {
        Reflection.FieldAccessor<Object> f = Reflection.getSimpleField(Reflection.getMinecraftClass("Blocks"), name.toUpperCase());
        durabilityField.set(f.get(null), Float.valueOf(durability));
    }

    public static final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static final Block getTargetBlock(Player player, int range, List<Material> materials) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return materials.contains(lastBlock.getType()) ? lastBlock : null;
    }
}
