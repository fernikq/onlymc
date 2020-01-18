package pl.fernikq.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;

import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final CorePlugin plugin;

    public TeleportManager(CorePlugin plugin){
        this.plugin = plugin;
    }

    private Map<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();

    public boolean teleportToLocation(Player player, Location l, int delay, String location) {
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(user.canByGroup(UserGroup.MOD)){
               player.teleport(l);
               ChatUtil.sendMessage(player, MessagesManager.teleportFinishLocationMessage.replace("{LOCATION}", location));
               return;
           }
            if(tasks.containsKey(player.getName().toLowerCase())) {
                ((BukkitTask)tasks.get(player.getName().toLowerCase())).cancel();
                tasks.remove(player.getName().toLowerCase());
                player.removePotionEffect(PotionEffectType.CONFUSION);
            }
            ChatUtil.sendMessage(player, MessagesManager.teleportStartMessage.replace("{TIME}", Integer.toString(delay)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, delay * 40, 1));
            tasks.put(player.getName().toLowerCase(), Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable(){
                int i = 0;
                Location loc = player.getLocation();
                @Override
                public void run() {
                    i++;
                    if(!player.isOnline() || player == null) {
                        ((BukkitTask)tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        return;
                    }
                    if(LocationUtil.move(loc, player.getLocation())) {
                        ChatUtil.sendMessage(player, MessagesManager.teleportCancelMessage);
                        ((BukkitTask)tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                    if(user.getUserFight().isDuringFight()){
                        ChatUtil.sendMessage(player, MessagesManager.error("Jestes podczas walki! Teleportacja anulowana"));
                        ((BukkitTask)tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                    if(i >= delay) {
                        player.teleport(l);
                        ChatUtil.sendMessage(player, MessagesManager.teleportFinishLocationMessage.replace("{LOCATION}", location));
                        ((BukkitTask)tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                }
            } ,0, 20));
        });
        return true;
    }

    public boolean teleportToPlayer(Player player, Player target, int delay) {
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.canByGroup(UserGroup.MOD)){
                player.teleport(target.getLocation());
                ChatUtil.sendMessage(player, MessagesManager.teleportFinishPlayerMessage.replace("{PLAYER}", target.getName()));
                return;
            }
            if(tasks.containsKey(player.getName().toLowerCase())) {
                ((BukkitTask) tasks.get(player.getName().toLowerCase())).cancel();
                tasks.remove(player.getName().toLowerCase());
                player.removePotionEffect(PotionEffectType.CONFUSION);
            }
            ChatUtil.sendMessage(player, MessagesManager.teleportStartMessage.replace("{TIME}", Integer.toString(delay)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, delay * 40, 1));
            tasks.put(player.getName().toLowerCase(), Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {
                int i = 0;
                Location loc = player.getLocation();
                Location targetLocation = target.getLocation();
                String targetName = target.getName();

                @Override
                public void run() {
                    i++;
                    if(!player.isOnline() || player == null) {
                        ((BukkitTask) tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        return;
                    }
                    if(LocationUtil.move(loc, player.getLocation())) {
                        ChatUtil.sendMessage(player, MessagesManager.teleportCancelMessage);
                        ((BukkitTask) tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                    if(user.getUserFight().isDuringFight()) {
                        ChatUtil.sendMessage(player, MessagesManager.error("Jestes podczas walki! Teleportacja anulowana"));
                        ((BukkitTask) tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                    if(i >= delay) {
                        player.teleport(targetLocation);
                        ChatUtil.sendMessage(player, MessagesManager.teleportFinishPlayerMessage.replace("{PLAYER}", targetName));
                        ((BukkitTask) tasks.get(player.getName().toLowerCase())).cancel();
                        tasks.remove(player.getName().toLowerCase());
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        return;
                    }
                }
            }, 0, 20));
        });
        return true;
    }
}
