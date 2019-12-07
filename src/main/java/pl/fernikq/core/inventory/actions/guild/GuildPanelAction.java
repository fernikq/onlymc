package pl.fernikq.core.inventory.actions.guild;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.enums.guild.GuildPanelActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.concurrent.TimeUnit;

public class GuildPanelAction implements InventoryAction {

    private Guild guild;
    private User user;
    private GuildMember member;
    private GuildMember otherMember;
    private CorePlugin plugin;
    private GuildPermission guildPermission;
    private GuildPanelActionType action;
    private int memberPermissionPage;
    private int coinsToPay;

    public GuildPanelAction(CorePlugin plugin, Guild guild, User user, GuildMember member, GuildMember otherMember, GuildPermission guildPermission, GuildPanelActionType action, int memberPermissionPage, int coinsToPay) {
        this.guild = guild;
        this.user = user;
        this.member = member;
        this.otherMember = otherMember;
        this.plugin = plugin;
        this.action = action;
        this.memberPermissionPage = memberPermissionPage;
        this.guildPermission = guildPermission;
        this.coinsToPay = coinsToPay;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack) {
        if(!user.hasGuild()){
            ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz gildii!"));
            player.closeInventory();
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_PANEL_MENU)){
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_MEMBER_CHOOSE)){
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Opcja dostepna jedynie dla lidera gildii!"));
                return;
            }
            InventoryGUI inventoryGUI = this.plugin.getGuildInventory().guildMemberChooseMenu(user, 1);
            if(inventoryGUI.isEmptyFirstSlot()){
                ChatUtil.sendMessage(player, MessagesManager.error("W twojej gildii nie ma czlonka ktorym mozesz zarzadzac"));
                return;
            }
            user.addInventory(inventoryGUI);
            inventoryGUI.openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_MEMBER_PERMISSION_NEXT_PAGE)) {
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Opcja dostepna jedynie dla lidera gildii!"));
                player.closeInventory();
                return;
            }
            InventoryGUI inventoryGUI = this.plugin.getGuildInventory().guildMemberChooseMenu(user, this.memberPermissionPage + 1);
            if(inventoryGUI.isEmptyFirstSlot()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz przejsc na kolejna strone!"));
                return;
            }
            user.addInventory(inventoryGUI);
            inventoryGUI.openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_MEMBER_PERMISSION_PREVIOUS_PAGE)) {
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Opcja dostepna jedynie dla lidera gildii!"));
                player.closeInventory();
                return;
            }
            if(this.memberPermissionPage <= 1){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz juz cofnac strony!"));
                return;
            }
            InventoryGUI inventoryGUI = this.plugin.getGuildInventory().guildMemberChooseMenu(user, this.memberPermissionPage - 1);
            if(inventoryGUI.isEmptyFirstSlot()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz juz cofnac strony!"));
                player.closeInventory();
                return;
            }
            user.addInventory(inventoryGUI);
            inventoryGUI.openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_MEMBER_PERMISSIONS)) {
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Opcja dostepna jedynie dla lidera gildii!"));
                player.closeInventory();
                return;
            }
            this.plugin.getGuildInventory().guildMemberPermission(user, otherMember).openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.CHANGE_PERMISSION)){
            if(!guild.getOwner().equals(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Opcja dostepna jedynie dla lidera gildii!"));
                player.closeInventory();
                return;
            }
            otherMember.changePermission(guildPermission);
            this.plugin.getGuildInventory().guildMemberPermission(user, otherMember).openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_RESOURCES)){
            this.plugin.getGuildInventory().guildResources(user).openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.GIVE_COINS_TO_RESOURCES)){
            if(user.getUserStat().getCoins() < this.coinsToPay){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz wystarczajacej ilosci monet!"));
                return;
            }
            this.guild.getTreasure().setCoins(this.guild.getTreasure().getCoins() + this.coinsToPay);
            user.getUserStat().removeCoins(this.coinsToPay);
            ChatUtil.sendMessage(player, "&8>> {n}Wplaciles {c}"+this.coinsToPay+" monet {n}do skarbca gildii&8!");
            this.plugin.getGuildInventory().guildResources(user).openInventory(player);
            return;
        }
        if(action.equals(GuildPanelActionType.EXECUTE_CUBOID_ENLARGE)){
            if(!member.hasPermission(GuildPermission.CUBOID_ENLARGE)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do powiekszania terenu gildii!"));
                return;
            }
            if(guild.getRegion().getEnlargeRegionLevel() > ConfigManager.guildCuboidSizeEnlargeCost.size() - 1){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia posiada maksymalny rozmiar terenu!"));
                return;
            }
            if(guild.getTreasure().getCoins() < ConfigManager.guildCuboidSizeEnlargeCost.get(guild.getRegion().getEnlargeRegionLevel())){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            guild.getRegion().setSize(guild.getRegion().getSize() + ConfigManager.guildCuboidSizeAddByEnlarge);
            guild.getTreasure().setCoins(guild.getTreasure().getCoins() - ConfigManager.guildCuboidSizeEnlargeCost.get(guild.getRegion().getEnlargeRegionLevel()));
            guild.getRegion().setEnlargeRegionLevel(guild.getRegion().getEnlargeRegionLevel() + 1);
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Powiekszyles {c}teren {n}gildii&8!");
            return;
        }
        if(action.equals(GuildPanelActionType.EXECUTE_ALLIANCES_ENLARGE)){
            if(!member.hasPermission(GuildPermission.ALLIANCES_ENLARGE)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do powiekszania maksymalnej ilosci sojuszy!"));
                return;
            }
            if(guild.getEnlargeAlliesLevel() > ConfigManager.guildAlliesSizeEnlargeCost.size() - 1){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia osiagnela maksymalna ilosci sojuszy!"));
                return;
            }
            if(guild.getTreasure().getCoins() < ConfigManager.guildAlliesSizeEnlargeCost.get(guild.getEnlargeAlliesLevel())){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            guild.setMaxAllies(guild.getMaxAllies() + 1);
            guild.getTreasure().setCoins(guild.getTreasure().getCoins() - ConfigManager.guildAlliesSizeEnlargeCost.get(guild.getEnlargeAlliesLevel()));
            guild.setEnlargeAlliesLevel(guild.getEnlargeAlliesLevel() + 1);
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Powiekszyles {c}ilosc sojuszy {n}gildii&8!");
            return;
        }
        if(action.equals(GuildPanelActionType.EXECUTE_MEMBERS_ENLARGE)){
            if(!member.hasPermission(GuildPermission.MEMBERS_ENLARGE)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do powiekszania maksymalnej ilosci czlonkow!"));
                return;
            }
            if(guild.getEnlargeMembersLevel() > ConfigManager.guildMembersSizeEnlargeCost.size() - 1){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia osiagnela maksymalna ilosci czlonkow!"));
                return;
            }
            if(guild.getTreasure().getCoins() < ConfigManager.guildMembersSizeEnlargeCost.get(guild.getEnlargeMembersLevel())){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            guild.setMaxMembers(guild.getMaxMembers() + 1);
            guild.getTreasure().setCoins(guild.getTreasure().getCoins() - ConfigManager.guildMembersSizeEnlargeCost.get(guild.getEnlargeMembersLevel()));
            guild.setEnlargeMembersLevel(guild.getEnlargeMembersLevel() + 1);
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Powiekszyles {c}ilosc czlonkow {n}gildii&8!");
            return;
        }
        if(action.equals(GuildPanelActionType.EXECUTE_TREASURE_ENLARGE)){
            if(!member.hasPermission(GuildPermission.TREASURE_ENLARGE)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do powiekszania skarbca!"));
                return;
            }
            if(guild.getTreasure().getLevel() >= 3){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia osiagnela maksymalny rozmiar skarbca!"));
                return;
            }
            if(guild.getTreasure().getCoins() < guild.getTreasure().getCostByLevel()){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            guild.getTreasure().setCoins(guild.getTreasure().getCoins() - guild.getTreasure().getCostByLevel());
            guild.getTreasure().setLevel(guild.getTreasure().getLevel() + 1);
            guild.getTreasure().recalculateTreasure();
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Powiekszyles {c}skarbiec {n}gildii&8!");
            return;
        }
        if(action.equals(GuildPanelActionType.EXECUTE_TIME_RENEW)){
            if(!member.hasPermission(GuildPermission.TIME_RENEW)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do przedluzania waznosci gildii!"));
                return;
            }
            if(guild.getTreasure().getCoins() < ConfigManager.guildTimeRenewCost){
                ChatUtil.sendMessage(player, MessagesManager.error("Twoja gildia nie posiada wystarczajacej ilosci monet!"));
                return;
            }
            if((guild.getExpireTime() - System.currentTimeMillis()) > TimeUtil.getTime(ConfigManager.guildMaxTimeRenew)){
                ChatUtil.sendMessage(player, MessagesManager.error("Waznosc gildii nie moze wynosic wiecej niz "+ConfigManager.guildMaxTimeRenew));
                return;
            }
            guild.getTreasure().setCoins(guild.getTreasure().getCoins() - ConfigManager.guildTimeRenewCost);
            guild.setExpireTime(guild.getExpireTime() + TimeUnit.DAYS.toMillis(1));
            this.plugin.getGuildInventory().guildMenu(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Przedluzyles {c}waznosci {n}gildii&8!");
            return;
        }
        if(action.equals(GuildPanelActionType.OPEN_TREASURE)){
            if(!member.hasPermission(GuildPermission.TREASURE_OPEN)){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz uprawnienia do skarbca!"));
                return;
            }
            player.openInventory(guild.getTreasure().getInventory());
            return;
        }
    }
}
