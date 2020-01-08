package pl.fernikq.core.inventory.actions.user;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.PlayerBackupActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.backup.Backup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

import java.util.Arrays;

public class PlayerBackupAction implements InventoryAction {

    private final CorePlugin plugin;
    private final PlayerBackupActionType actionType;
    private final Backup backup;
    private final User user;

    public PlayerBackupAction(CorePlugin plugin, PlayerBackupActionType actionType, Backup backup, User user){
        this.plugin = plugin;
        this.actionType = actionType;
        this.backup = backup;
        this.user = user;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(actionType.equals(PlayerBackupActionType.OPEN_BACKUP_MENU)){
            User backupUser = backup.getUser();
            this.plugin.getUserInventory().playerBackups(user, backupUser).openInventory(player);
            return;
        }
        if(actionType.equals(PlayerBackupActionType.CHOOSE_BACKUP)){
            this.plugin.getUserInventory().playerBackup(user, backup).openInventory(player);
            return;
        }
        if(actionType.equals(PlayerBackupActionType.ACCEPT_BACKUP)){
            User backupUser = backup.getUser();
            Player backupPlayer = backupUser.asPlayer();
            if(backupPlayer == null){
                ChatUtil.sendMessage(player, Lang.playerOffline);
                return;
            }
            if(this.backup.isGivePoints()){
                backupUser.getUserStat().setPoints(this.backup.getPoints());
            }
            if(this.backup.isGiveDeaths()){
                backupUser.getUserStat().setDeaths(this.backup.getDeaths());
            }
            if(this.backup.isGiveArmor()){
                backupPlayer.getInventory().setArmorContents(this.backup.getArmor());
            }
            if(this.backup.isGiveItems()){
                backupPlayer.getInventory().setContents(this.backup.getItems());
            }
            this.plugin.getUserManager().getBackupData().deleteBackup(this.backup);
            backupUser.getBackups().remove(this.backup);
            ChatUtil.sendMessage(player, "&8>> {n}Przyznano backup dla gracza {c}"+backupPlayer.getName());
            player.closeInventory();
            return;
        }
        if(actionType.equals(PlayerBackupActionType.CHANGE_ARMOR)){
            this.backup.setGiveArmor(!this.backup.isGiveArmor());
            this.plugin.getUserInventory().playerBackup(user, this.backup).openInventory(player);
            return;
        }
        if(actionType.equals(PlayerBackupActionType.CHANGE_DEATHS)){
            this.backup.setGiveDeaths(!this.backup.isGiveDeaths());
            this.plugin.getUserInventory().playerBackup(user, this.backup).openInventory(player);
            return;
        }
        if(actionType.equals(PlayerBackupActionType.CHANGE_ITEMS)){
            this.backup.setGiveItems(!this.backup.isGiveItems());
            this.plugin.getUserInventory().playerBackup(user, this.backup).openInventory(player);
            return;
        }
        if(actionType.equals(PlayerBackupActionType.CHANGE_POINTS)){
            this.backup.setGivePoints(!this.backup.isGivePoints());
            this.plugin.getUserInventory().playerBackup(user, this.backup).openInventory(player);
            return;
        }
    }
}
