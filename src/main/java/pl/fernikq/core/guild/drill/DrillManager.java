package pl.fernikq.core.guild.drill;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.SpaceUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DrillManager {

    private final CorePlugin plugin;
    private ItemStack drill;
    private GuildDrillData data;
    private Map<GuildDrill, BukkitTask> drills;

    public DrillManager(CorePlugin plugin){
        this.plugin = plugin;
        this.drill = new ItemBuilder(Material.HOPPER).setName(ChatUtil.fixColor("&8[ &c&lWiertlo Gildii &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fWiertlo pomaga w zdobywaniu surowcow", "&8>> &fPostaw je na terenie swojej gildii", "&8>> &fI zacznij zdobywac cenne mineraly", " ")))
                .addEnchant(Enchantment.DIG_SPEED, 10).toItemStack();
        this.drills = new HashMap<>();
    }

    public void init(){
        this.data = new GuildDrillData(this.plugin);
    }

    public void registerDrillTask(GuildDrill drill){
        if(this.drills.containsKey(drill)){
            ((BukkitTask)this.drills.get(drill)).cancel();
        }
        this.drills.put(drill, this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            drill.getInventory().addItem(new ItemStack(drill.getMaterial(), ThreadLocalRandom.current().nextInt(drill.getAmountByLevel()[0], drill.getAmountByLevel()[1])));
        }, drill.getSpeedByLevel(), drill.getSpeedByLevel()));
    }

    public void unregisterDrillTask(GuildDrill drill){
        if(this.drills.containsKey(drill)){
            ((BukkitTask)this.drills.get(drill)).cancel();
        }
        this.drills.remove(drill);
    }

    public void deleteGuildDrill(GuildDrill guildDrill){
        for(Location loc : SpaceUtil.getSquare(guildDrill.getCenter().clone().subtract(0, 2, 0), 2, 6)){
            loc.getBlock().setType(Material.AIR);
        }
    }

    public boolean canPlaceDrill(Location location){
        for(Location loc : SpaceUtil.getSquare(location.clone(), 2, 4)){
            if(loc.getBlock().getType() == Material.HOPPER) continue;
            if(loc.getBlock().getType() != Material.AIR) return false;
        }
        return true;
    }

    public boolean canPlaceDrillCauseGuildRegion(Location location, Guild guild){
        return location.toVector().isInAABB(guild.getRegion().getLowerCorner().clone().add(2, 0, 2).toVector(), guild.getRegion().getUpperCorner().clone().subtract(2, 0, 2).toVector());
    }

    public boolean canPlaceDrillCauseGuildCenter(Location location, Guild guild){
        for(Location loc : SpaceUtil.getSquare(location.clone().subtract(0, 1, 0), 2, 5)){
            if(guild.getRegion().isInCenter(loc)) return false;
        }
        return true;
    }

    public void createGuildDrill(Location location){//TODO
        Location drillLocation = location.clone();
        for(Location loc : SpaceUtil.getSquare(drillLocation, 2, 4)){
            loc.getBlock().setType(Material.AIR);
        }
        drillLocation.setY(drillLocation.getY() - 1);
        for(Location loc : SpaceUtil.getSquare(drillLocation, 2)){
            loc.getBlock().setType(Material.IRON_BLOCK);
        }
        for(Location loc : SpaceUtil.getSquare(drillLocation, 1, 0)){
            loc.getBlock().setType(Material.STONE);
        }
        drillLocation.setY(location.getY());
        drillLocation.getBlock().setType(Material.HOPPER);
        drillLocation.setY(drillLocation.getY() - 1);
        drillLocation.getBlock().setType(Material.BEDROCK);
        drillLocation.setY(drillLocation.getY() + 2);
        drillLocation.getBlock().setType(Material.CAULDRON);
        drillLocation.setY(drillLocation.getY() + 1);
        drillLocation.getBlock().setType(Material.IRON_FENCE);
        drillLocation.setY(drillLocation.getY() + 1);
        drillLocation.getBlock().setType(Material.COBBLE_WALL);
        drillLocation.setX(drillLocation.getX() + 1);
        drillLocation.getBlock().setType(Material.COBBLE_WALL);
        drillLocation.setY(drillLocation.getY() - 1);
        drillLocation.getBlock().setType(Material.COBBLE_WALL);
        drillLocation.setY(drillLocation.getY() - 1);
        drillLocation.getBlock().setType(Material.COBBLESTONE);
        BlockFace[] blockFaces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
        for(BlockFace blockFace : blockFaces){
            Block block = drillLocation.getBlock().getRelative(blockFace);
            if(block.getType() != Material.AIR){
                continue;
            }
            block.setType(Material.IRON_FENCE);
        }
        Material material = Material.IRON_FENCE;
        drillLocation.setY(drillLocation.getY() - 1);
        drillLocation.getBlock().setType(Material.COBBLE_WALL);
    }

    public boolean isSimilar(ItemStack itemStack){
        return this.drill.isSimilar(itemStack);
    }

    public ItemStack getDrill() {
        return drill;
    }

    public Map<GuildDrill, BukkitTask> getDrills() {
        return new HashMap<>(this.drills);
    }
}
