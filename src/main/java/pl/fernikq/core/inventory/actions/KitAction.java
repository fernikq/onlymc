package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.KitActionType;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

public class KitAction implements InventoryAction {

    private final CorePlugin plugin;
    private final Kit kit;
    private final KitActionType kitActionType;

    public KitAction(CorePlugin plugin, Kit kit, KitActionType type){
        this.plugin = plugin;
        this.kit = kit;
        this.kitActionType = type;
    }

    public KitAction(CorePlugin plugin, KitActionType type){
        this.plugin = plugin;
        this.kit = null;
        this.kitActionType = type;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(kitActionType.equals(KitActionType.CHOOSE)){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().kit(user, kit).openInventory(player);
            });
            return;
        }
        if(kitActionType.equals(KitActionType.BACK)){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                this.plugin.getUserInventory().kitMenu(user).openInventory(player);
            });
            return;
        }
        if(kitActionType.equals(KitActionType.TAKE)){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                if(!this.plugin.getKitManager().canTakeByGroup(user, kit)){
                    ChatUtil.sendMessage(player, "&8>> {n}Aby odebrac ten zestaw musisz miec range "+kit.getGroup().getPrefix()+(kit.canRankHigher() ? " {n}lub wyzsza!" : ""));
                    return;
                }
                if(!this.plugin.getKitManager().canTakeByTime(user, kit)){
                    ChatUtil.sendMessage(player, "&8>> {n}Zestaw mozesz odebrac za&8: {c}"+ TimeUtil.getTimeToString(user.getKitTimes().get(kit.getName()) - System.currentTimeMillis()));
                    return;
                }
                this.plugin.getKitManager().giveItems(player, kit);
                ChatUtil.sendMessage(player, "&8>> {n}Odebrales zestaw "+kit.getName());
                user.addKitTime(kit.getName(), kit.getTime() + System.currentTimeMillis());
                player.closeInventory();
                return;
            });
            return;
        }
    }
}
