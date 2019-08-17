package pl.fernikq.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;

import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final CorePlugin plugin;

    public TeleportManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    private Map<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();

    public boolean teleportToLocation(Player p, Location l, int delay, String location) {
        if(tasks.containsKey(p.getName().toLowerCase())) {
            ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
            tasks.remove(p.getName().toLowerCase());
            p.removePotionEffect(PotionEffectType.CONFUSION);
        }
        ChatUtil.sendMessage(p, MessagesManager.teleportStartMessage.replace("{TIME}", Integer.toString(delay)));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, delay * 40, 1));
        tasks.put(p.getName().toLowerCase(), Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable(){
            int i = 0;
            Location loc = p.getLocation();
            @Override
            public void run() {
                i++;
                if(!p.isOnline() || p == null) {
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    return;
                }
                if(LocationUtil.move(loc, p.getLocation())) {
                    ChatUtil.sendMessage(p, MessagesManager.teleportCancelMessage);
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    return;
                }
                if(i >= delay) {
                    p.teleport(l);
                    ChatUtil.sendMessage(p, MessagesManager.teleportFinishLocationMessage.replace("{LOCATION}", location));
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    return;
                }
            }
        } ,0, 20));
        return true;
    }

    public boolean teleportToPlayer(Player p, Player target, int delay) {
        if(tasks.containsKey(p.getName().toLowerCase())) {
            ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
            tasks.remove(p.getName().toLowerCase());
            p.removePotionEffect(PotionEffectType.CONFUSION);
        }
        ChatUtil.sendMessage(p, MessagesManager.teleportStartMessage.replace("{TIME}", Integer.toString(delay)));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, delay * 40, 1));
        tasks.put(p.getName().toLowerCase(), Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable(){
            int i = 0;
            Location loc = p.getLocation();
            Location targetLocation = target.getLocation();
            String targetName = target.getName();
            @Override
            public void run() {
                i++;
                if(!p.isOnline() || p == null) {
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    return;
                }
                if(LocationUtil.move(loc, p.getLocation())) {
                    ChatUtil.sendMessage(p, MessagesManager.teleportCancelMessage);
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    return;
                }
                if(i >= delay) {
                    p.teleport(targetLocation);
                    ChatUtil.sendMessage(p, MessagesManager.teleportFinishLocationMessage.replace("{PLAYER}", targetName));
                    ((BukkitTask)tasks.get(p.getName().toLowerCase())).cancel();
                    tasks.remove(p.getName().toLowerCase());
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    return;
                }
            }
        } ,0, 20));
        return true;
    }
}
