package pl.fernikq.core.inventory.guild;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.guild.member.GuildMember;
import pl.fernikq.core.guild.member.GuildPermission;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.TopsAction;
import pl.fernikq.core.inventory.actions.guild.GuildPanelAction;
import pl.fernikq.core.inventory.enums.TopsActionType;
import pl.fernikq.core.inventory.enums.guild.GuildPanelActionType;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuildInventory {

    private final CorePlugin plugin;
    private ItemStack blank;
    private ItemStack color;
    private ItemStack backGlass;
    private ItemStack backBarrier;
    private String customHeadName;

    public GuildInventory(CorePlugin plugin){
        this.plugin = plugin;
        blank = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).setName(" ").toItemStack();
        color = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(" ").toItemStack();
        backGlass = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        backBarrier = new ItemBuilder(new ItemStack(Material.BARRIER, 1)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        customHeadName = "King";
    }

    public InventoryGUI guildItems(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lPrzedmioty na gildie &8]", 3, true);
        user.addInventory(gui);
        int slot = 9;
        if(user.canByGroup(UserGroup.VIP)) {
            for(String item : ConfigManager.guildVipItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                int have = ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), material, data);
                String name = itemInfo[3];
                gui.setItem(slot, new ItemBuilder(material, amount, data).setName(ChatUtil.fixColor("{c}"+name)).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}"+have+"&8/{c}"+amount))).toItemStack());
                if(have > amount){
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                }else{
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                }
                slot++;
            }
        }else{
            for(String item : ConfigManager.guildPlayerItemsToCreate) {
                String[] itemInfo = item.split(":");
                Material material = ItemUtil.getMaterial(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[2]);
                short data = Short.parseShort(itemInfo[1]);
                int have = ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), material, data);
                String name = itemInfo[3];
                gui.setItem(slot, new ItemBuilder(material, amount, data).setName(ChatUtil.fixColor("{c}"+name)).toItemStack());
                if(have > amount){
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).setName(ChatUtil.fixColor("&aPosiadasz")).toItemStack());
                }else{
                    gui.setItem(slot - 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                    gui.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).setName(ChatUtil.fixColor("&cNie posiadasz")).toItemStack());
                }
                slot++;
            }
        }
        return gui;
    }

    public InventoryGUI guildMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lPanel Gildii &8]", 3, true);
        user.addInventory(gui);
        Guild guild = user.getGuild();
        GuildMember member = guild.getMemberByName(user.getName()).orElse(null);
        ItemBuilder memberManagement = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(customHeadName).setDurability((short)3).setName(ChatUtil.fixColor("{c}&lZarzadzanie czlonkami"));
        if(guild.getOwner().equals(user)){
            memberManagement.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zarzadzac czlonkami gildii&8!")));
        }else{
            memberManagement.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Opcja dla lidera gildii&8!")));
        }
        ItemBuilder guildResources = new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lZasoby gildii"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby sprawdzic&8!")));
        ItemBuilder guildTreasure = new ItemBuilder(Material.CHEST).setName(ChatUtil.fixColor("{c}&lSkarbiec gildii"));
        if(member.hasPermission(GuildPermission.TREASURE_OPEN)){
            guildTreasure.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby otworzyc&8!")));
        }else{
            guildTreasure.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nie posiadasz uprawnienia&8!!")));
        }
        ItemBuilder guildTimeRenew = new ItemBuilder(Material.WATCH).setName(ChatUtil.fixColor("{c}&lPrzedluz waznosc"));
        if(member.hasPermission(GuildPermission.TIME_RENEW)){
            guildTimeRenew.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gildia wygasa za&8: {c}"+ TimeUtil.getTimeToString(guild.getExpireTime() - System.currentTimeMillis()),
                    " ", ((guild.getExpireTime() - System.currentTimeMillis()) < TimeUtil.getTime(ConfigManager.guildMaxTimeRenew) ? "&8>> {n}Koszt&8: {c}"+ConfigManager.guildTimeRenewCost : "&8>> {n}Nie mozesz juz przedluzyc waznosci&8!"))));
        }else{
            guildTimeRenew.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gildia wygasa za&8: {c}"+ TimeUtil.getTimeToString(guild.getExpireTime() - System.currentTimeMillis()), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        ItemBuilder guildRegionEnlarge = new ItemBuilder(Material.GRASS).setName(ChatUtil.fixColor("{c}&lPowieksz teren"));
        if(member.hasPermission(GuildPermission.CUBOID_ENLARGE)){
            guildRegionEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny rozmiar&8: {c}"+guild.getRegion().getSize()+"&8x{c}"+guild.getRegion().getSize(), " ",
                    (guild.getRegion().getEnlargeRegionLevel() > ConfigManager.guildCuboidSizeEnlargeCost.size() - 1 ? "&8{n}Gildia posiada maksymalny region&8!" : "&8>> {n}Koszt&8: {c}"+ConfigManager.guildCuboidSizeEnlargeCost.get(guild.getRegion().getEnlargeRegionLevel())))));
        }else{
            guildRegionEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny rozmiar&8: {c}"+guild.getRegion().getSize()+"&8x{c}"+guild.getRegion().getSize(), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        ItemBuilder guildMembersEnlarge = new ItemBuilder(Material.BOOK).setName(ChatUtil.fixColor("{c}&lPowieksz ilosc miejsc"));
        if(member.hasPermission(GuildPermission.MEMBERS_ENLARGE)){
            guildMembersEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualna ilosc&8: {c}"+guild.getMaxMembers(), " ",
                    (guild.getEnlargeMembersLevel() > ConfigManager.guildMembersSizeEnlargeCost.size() - 1 ? "&8{n}Gildia posiada maksymalna ilosc czlonkow&8!" : "&8>> {n}Koszt&8: {c}"+ConfigManager.guildMembersSizeEnlargeCost.get(guild.getEnlargeMembersLevel())))));
        }else{
            guildMembersEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualna ilosc&8: {c}"+guild.getMaxMembers(), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        ItemBuilder guildAlliancesEnlarge = new ItemBuilder(Material.GOLD_SWORD).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setName(ChatUtil.fixColor("{c}&lPowieksz ilosc sojuszy"));
        if(member.hasPermission(GuildPermission.ALLIANCES_ENLARGE)){
            guildAlliancesEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualna ilosc&8: {c}"+guild.getMaxAllies(), " ",
                    (guild.getEnlargeAlliesLevel() > ConfigManager.guildAlliesSizeEnlargeCost.size() - 1 ? "&8{n}Gildia posiada maksymalna ilosc sojuszy&8!" : "&8>> {n}Koszt&8: {c}"+ConfigManager.guildAlliesSizeEnlargeCost.get(guild.getEnlargeAlliesLevel())))));
        }else{
            guildAlliancesEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualna ilosc&8: {c}"+guild.getMaxAllies(), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        ItemBuilder guildTreasureEnlarge = new ItemBuilder(Material.ENDER_CHEST).setName(ChatUtil.fixColor("{c}&lPowieksz skarbiec"));
        if(member.hasPermission(GuildPermission.TREASURE_ENLARGE)){
            guildTreasureEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny poziom&8: {c}"+(guild.getTreasure().getLevel()+1), " ", (guild.getTreasure().getLevel() < 3 ? "&8>> {n}Koszt&8: {c}"+guild.getTreasure().getCostByLevel() : "&8{n}Gildia posiada maksymalna poziom skarbca&8!"))));
        }else{
            guildTreasureEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny poziom&8: {c}"+(guild.getTreasure().getLevel()+1), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        gui.setItem(1, memberManagement.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.OPEN_MEMBER_CHOOSE, 0, 0));
        gui.setItem(3, guildResources.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.OPEN_RESOURCES, 0, 0));
        gui.setItem(5, guildTreasure.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.OPEN_TREASURE, 0, 0));
        gui.setItem(7, guildTimeRenew.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.EXECUTE_TIME_RENEW, 0, 0));
        gui.setItem(19, guildRegionEnlarge.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.EXECUTE_CUBOID_ENLARGE, 0, 0));
        gui.setItem(21, guildTreasureEnlarge.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.EXECUTE_TREASURE_ENLARGE, 0, 0));
        gui.setItem(23, guildMembersEnlarge.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.EXECUTE_MEMBERS_ENLARGE, 0, 0));
        gui.setItem(25, guildAlliancesEnlarge.toItemStack(), new GuildPanelAction(this.plugin, guild, user, member, null, null, GuildPanelActionType.EXECUTE_ALLIANCES_ENLARGE, 0, 0));
        gui.setItem(10, color.clone());
        gui.setItem(12, color.clone());
        gui.setItem(14, color.clone());
        gui.setItem(16, color.clone());
        gui.setEmptyItem(blank.clone());
        return gui;
    }

    public InventoryGUI guildMemberChooseMenu(User user, int page){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lWybor czlonka &8]", 6, true);
        Guild guild = user.getGuild();
        GuildMember userMember = guild.getMemberByName(user.getName()).orElse(null);
        List<GuildMember> guildMemberList = guild.getMembers().stream().filter(guildMember -> !guildMember.getUser().equals(guild.getOwner())).collect(Collectors.toList());
        for(int i = (page - 1) * 45; i < page * 45; i++) {
            if(guildMemberList.size() > 0 && i <= guildMemberList.size() - 1) {
                GuildMember member = guildMemberList.get(i);
                ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{c}&l" + member.getUser().getName())).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zarzadzac&8!"))).setSkullOwner(member.getUser().getName());
                gui.addItem(head.toItemStack(), new GuildPanelAction(this.plugin, guild, user, userMember, member, null, GuildPanelActionType.OPEN_MEMBER_PERMISSIONS, page, 0));
                continue;
            }
            break;
        }
        gui.setItem(50, new ItemBuilder(Material.SKULL_ITEM).setDurability((short)3).setSkullOwner("MHF_ArrowRight").setName(ChatUtil.fixColor("&a&lDalej")).toItemStack(),
                new GuildPanelAction(this.plugin, guild, user, userMember, null, null, GuildPanelActionType.OPEN_MEMBER_PERMISSION_NEXT_PAGE, page, 0));
        gui.setItem(49, backGlass.clone(), new GuildPanelAction(this.plugin, guild, user, userMember, null, null, GuildPanelActionType.OPEN_PANEL_MENU, page, 0));
        gui.setItem(48, new ItemBuilder(Material.SKULL_ITEM).setDurability((short)3).setSkullOwner("MHF_ArrowLeft").setName(ChatUtil.fixColor("&c&lWstecz")).toItemStack(),
                new GuildPanelAction(this.plugin, guild, user, userMember, null, null, GuildPanelActionType.OPEN_MEMBER_PERMISSION_PREVIOUS_PAGE, page, 0));
        for(int i = 45; i < 54; i++){
            gui.setItemIfEmpty(i, blank.clone());
        }
        return gui;
    }

    public InventoryGUI guildMemberPermission(User user, GuildMember member){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lUprawnienia czlonka &8]", 3, true);
        user.addInventory(gui);
        Guild guild = user.getGuild();
        GuildMember userMember = guild.getMemberByName(user.getName()).orElse(null);
        for(GuildPermission guildPermission : GuildPermission.values()) {
            gui.addItem(new ItemBuilder(guildPermission.getMaterial()).setName(ChatUtil.fixColor(guildPermission.getName())).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiada&8: "+(member.hasPermission(guildPermission) ? "&aTak" : "&cNie")))).toItemStack(),
                    new GuildPanelAction(this.plugin, guild, user, userMember, member, guildPermission, GuildPanelActionType.CHANGE_PERMISSION, 0, 0));
        }
        gui.setItem(26, backGlass.clone(), new GuildPanelAction(this.plugin, guild, user, userMember, member, null, GuildPanelActionType.OPEN_MEMBER_CHOOSE, 0, 0));
        return gui;
    }

    public InventoryGUI guildResources(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lZasoby gildii &8]", 1, true);
        user.addInventory(gui);
        Guild guild = user.getGuild();
        gui.setItem(4, new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lMonety")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gildia posiada&8: {c}"+guild.getTreasure().getCoins()+" {n}monet"))).toItemStack());
        gui.setItem(3, backGlass.clone(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.OPEN_PANEL_MENU, 0, 0));
        gui.setItem(5, backGlass.clone(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.OPEN_PANEL_MENU, 0, 0));
        gui.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}100 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 100));
        gui.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}500 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 500));
        gui.setItem(2, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}1000 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 1000));
        gui.setItem(6, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}2000 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 2000));
        gui.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}5000 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 5000));
        gui.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("{n}Wplac {c}10000 {n}monet")).toItemStack(), new GuildPanelAction(this.plugin, guild, user, null, null, null, GuildPanelActionType.GIVE_COINS_TO_RESOURCES, 0, 10000));
        return gui;
    }

    public InventoryGUI guildTops(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lTOPKI - WYBOR &8]", 3, true);
        user.addInventory(gui);
        this.plugin.getTopManager().getTopsByKind(TopKind.GUILD).forEach(sortable -> {
            gui.addItem(new ItemBuilder(sortable.getTopType().getMaterial()).setName(ChatUtil.fixColor("{c}&l"+sortable.getTopType().getName())).toItemStack(), new TopsAction(this.plugin, sortable, TopsActionType.CHOOSE_GUILD_TOP, user));
        });
        gui.setItem(26, this.backGlass.clone(), new TopsAction(this.plugin, null, TopsActionType.BACK_TO_MAIN_MENU, user));
        return gui;
    }

    public InventoryGUI guildTop(User user, Sortable<Guild> top){
        InventoryGUI inventoryGUI = top.getInventory(user.getGuild());
        inventoryGUI.setItem(48, this.backGlass, new TopsAction(this.plugin, top, TopsActionType.OPEN_GUILD_TOPS_SELECT, user));
        inventoryGUI.setItem(50, this.backGlass, new TopsAction(this.plugin, top, TopsActionType.OPEN_GUILD_TOPS_SELECT, user));
        inventoryGUI.setEmptyItem(this.blank);
        user.addInventory(inventoryGUI);
        return inventoryGUI;
    }
}
