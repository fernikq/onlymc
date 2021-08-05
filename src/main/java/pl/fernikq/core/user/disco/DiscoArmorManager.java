package pl.fernikq.core.user.disco;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.RandomUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DiscoArmorManager {

    private final CorePlugin plugin;

    private final ConcurrentMap<UUID, BukkitTask> discoRunnables = new ConcurrentHashMap<>();

    public DiscoArmorManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    public void startTask(User user){
        DiscoArmor discoArmor = new DiscoArmor();
        Player player = user.asPlayer();
        ItemStack[] coloredArmor = new ItemStack[]{new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)};
        this.discoRunnables.putIfAbsent(user.getUuid(), this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if(!user.isDiscoArmorPermission() || !user.isDiscoArmor() || !user.isOnline()){
                    stopTask(player);
                    return;
                }
                Color nextColor = DiscoArmorColorUtil.nextColor(discoArmor.getLastColor());
                player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 30, 50, 30).stream().filter(entity -> entity.getType() == EntityType.PLAYER)
                    .forEach(entity -> {
                        if(entity.getUniqueId().equals(player.getUniqueId())) return;
                        for(int i = 0; i < 4; i++){
                            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)coloredArmor[i].getItemMeta();
                            leatherArmorMeta.setColor(nextColor);
                            coloredArmor[i].setItemMeta(leatherArmorMeta);
                            sendChange((Player) entity, coloredArmor[i], player.getEntityId(), i + 1);
                        }
                    });
                discoArmor.setLastColor(DiscoArmorColorUtil.nextColor(discoArmor.getLastColor()));
            }
        }, 0, 2));
    }

    public void stopTask(Player player){
        if(!this.discoRunnables.containsKey(player.getUniqueId())) return;
        this.discoRunnables.get(player.getUniqueId()).cancel();
        this.discoRunnables.remove(player.getUniqueId());
        player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 60, 50, 60).stream().filter(entity -> entity.getType() == EntityType.PLAYER)
                .forEach(entity -> restoreArmor((Player) entity, player));
    }

    public void restoreArmor(Player target, Player source){
        if(target.getUniqueId().equals(source.getUniqueId())) return;
        for(int i = 0; i < 4; i++){
            PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(source.getEntityId(), i + 1, CraftItemStack.asNMSCopy(source.getInventory().getArmorContents()[i]));
            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
        }
    }

    public void sendChange(Player player, ItemStack itemStack, int entityID, int armorSlot){
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(entityID, armorSlot, CraftItemStack.asNMSCopy(itemStack));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
    }
}
