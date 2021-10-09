package pl.fernikq.core.inventory.actions.guild;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.enums.guild.GuildLogblockActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class GuildLogblockAction implements InventoryAction {

    private User user;
    private int page;
    private CorePlugin plugin;
    private Location location;
    private Guild guild;
    private GuildLogblockActionType type;

    public GuildLogblockAction(CorePlugin plugin, User user, Guild guild, Location location, GuildLogblockActionType type, int page) {
        this.plugin = plugin;
        this.user = user;
        this.guild = guild;
        this.location = location;
        this.type = type;
        this.page = page;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        event.setCancelled(true);
        if(this.type.equals(GuildLogblockActionType.NEXT.NEXT)){
            InventoryGUI gui = this.plugin.getGuildInventory().guildLogBlockInventory(this.user, this.location, this.guild, this.page + 1);
            if(gui.isEmptyFirstSlot()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz przewinac strony!"));
                return;
            }
            user.addInventory(gui);
            gui.openInventory(player);
        }
        if(this.type.equals(GuildLogblockActionType.BACK)){
            if(this.page <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz juz cofnac strony!"));
                return;
            }
            InventoryGUI gui = this.plugin.getGuildInventory().guildLogBlockInventory(this.user, this.location, this.guild, this.page - 1);
            user.addInventory(gui);
            gui.openInventory(player);
        }
    }
}
