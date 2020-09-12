package pl.fernikq.core.inventory.actions.user;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.IncognitoActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.incognito.IncognitoType;
import pl.fernikq.core.user.incognito.UserIncognito;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

import java.util.concurrent.TimeUnit;

public class BlocksExchangeAction implements InventoryAction {

    private User user;
    private CorePlugin plugin;
    private Material material;

    public BlocksExchangeAction(CorePlugin plugin, Material material, User user){
        this.user = user;
        this.plugin = plugin;
        this.material = material;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        int amount = ItemUtil.getAmountOfMaterial(player.getInventory(), this.material);
        if(amount < 9){
            ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz odpowiedniej ilosci tego przedmiotu!"));
            return;
        }
        int can = amount / 9;
        ItemUtil.remove(new ItemStack(this.material), player, can * 9);
        ItemUtil.giveItems(player, new ItemStack(this.getMaterial(), can));
        ChatUtil.sendMessage(player, "&8>> &fPomyslnie dokonales wymiany surowcow na bloki!");
    }

    private Material getMaterial(){
        switch(this.material){
            case DIAMOND: return Material.DIAMOND_BLOCK;
            case IRON_INGOT: return Material.IRON_BLOCK;
            case EMERALD: return Material.EMERALD_BLOCK;
            case REDSTONE: return Material.REDSTONE_BLOCK;
            case GOLD_INGOT: return Material.GOLD_BLOCK;
        }
        return null;
    }
}
