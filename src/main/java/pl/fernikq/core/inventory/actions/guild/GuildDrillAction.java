package pl.fernikq.core.inventory.actions.guild;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.drill.GuildDrill;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.guild.GuildDrillActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class GuildDrillAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final GuildDrillActionType actionType;
    private final GuildDrill guildDrill;
    private final Material material;
    private final Guild guild;

    public GuildDrillAction(CorePlugin plugin, GuildDrill guildDrill, GuildDrillActionType actionType, Material material, Guild guild, User user){
        this.plugin = plugin;
        this.user = user;
        this.actionType = actionType;
        this.guildDrill = guildDrill;
        this.material = material;
        this.guild = guild;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(!this.guild.getOwner().equals(this.user)){
            player.closeInventory();
            return;
        }
        if(this.actionType == GuildDrillActionType.OPEN_MENU){
            this.plugin.getGuildInventory().guildDrillMenu(this.user, this.guildDrill, this.guild).openInventory(player);
            return;
        }
        if(this.actionType == GuildDrillActionType.CHANGE_MATERIAL_MENU){
            this.plugin.getGuildInventory().guildDrillMaterialSwitch(this.user, this.guildDrill, this.guild).openInventory(player);
            return;
        }
        if(this.actionType == GuildDrillActionType.CHANGE_MATERIAL){
            this.guildDrill.setMaterial(this.material);
            this.plugin.getGuildInventory().guildDrillMaterialSwitch(this.user, this.guildDrill, this.guild).openInventory(player);
            return;
        }
        if(this.actionType == GuildDrillActionType.UPGRADE_DRILL){
            if(this.guildDrill.getLevel() >= 3){
                ChatUtil.sendMessage(player, MessagesManager.error("Wiertlo posiada juz maksymalne ulepszenie!"));
                return;
            }
            if(ConfigManager.guildDrillUpgradeCost.size() < 3){
                ChatUtil.sendMessage(player, MessagesManager.error("Wystapil blad, zglos sie do administracji!"));
                return;
            }
            if(this.guild.getTreasure().getCoins() < ConfigManager.guildDrillUpgradeCost.get(this.guildDrill.getLevel())){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            this.guild.getTreasure().setCoins(this.guild.getTreasure().getCoins() - ConfigManager.guildDrillUpgradeCost.get(this.guildDrill.getLevel()));
            this.guildDrill.setLevel(this.guildDrill.getLevel() + 1);
            this.plugin.getDrillManager().unregisterDrillTask(this.guildDrill);
            this.plugin.getDrillManager().registerDrillTask(this.guildDrill);
            this.plugin.getGuildInventory().guildDrillMenu(this.user, this.guildDrill, this.guild).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> &fUlepszyles wiertlo gilldii na {c}&lpoziom "+this.guildDrill.getLevel());
            return;
        }
        if(this.actionType == GuildDrillActionType.REMOVE_DRILL){
            this.guild.removeDrill(this.guildDrill);
            this.plugin.getDrillManager().unregisterDrillTask(this.guildDrill);
            this.plugin.getDrillManager().deleteGuildDrill(this.guildDrill);
            ItemUtil.giveItems(player, this.plugin.getDrillManager().getDrill().clone());
            for(ItemStack content : this.guildDrill.getInventory().getContents()) {
                if(content == null) continue;
                this.guildDrill.getCenter().getWorld().dropItemNaturally(this.guildDrill.getCenter(), content);
            }
            ChatUtil.sendMessage(player, "&8>> &fPomyslnie usunales wiertlo gildii");
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDrillManager().getData().delete(this.guildDrill));
            player.closeInventory();
            return;
        }
        if(this.actionType == GuildDrillActionType.OPEN_TREASURE){
            player.openInventory(this.guildDrill.getInventory());
            return;
        }
        if(this.actionType == GuildDrillActionType.SWITCH_DRILL_WORK){
            if(this.plugin.getDrillManager().getDrills().containsKey(this.guildDrill)){
                this.plugin.getDrillManager().unregisterDrillTask(this.guildDrill);
                ChatUtil.sendMessage(player, "&8>> &fPomyslnie wylaczyles wiertlo");
                this.plugin.getGuildInventory().guildDrillMenu(this.user, this.guildDrill, this.guild).openInventory(player);
                return;
            }
            this.plugin.getDrillManager().registerDrillTask(this.guildDrill);
            ChatUtil.sendMessage(player, "&8>> &fPomyslnie wlaczyles wiertlo");
            this.plugin.getGuildInventory().guildDrillMenu(this.user, this.guildDrill, this.guild).openInventory(player);
            return;
        }
    }
}
