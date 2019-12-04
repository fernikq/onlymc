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
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.TimeUtil;

import java.util.Arrays;

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
        if(member.hasPermission(GuildPermission.MEMBER_PERMISSIONS)){
            memberManagement.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zarzadzac czlonkami gildii&8!")));
        }else{
            memberManagement.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nie posiadasz uprawnienia&8!")));
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
            guildTreasureEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny poziom&8: {c}"+(guild.getTreasure().getLevel()+1), " ", (guild.getTreasure().getLevel() < 2 ? "&8>> {n}Koszt&8: {c}"+guild.getTreasure().getCostByLevel() : "&8{n}Gildia posiada maksymalna poziom skarbca&8!"))));
        }else{
            guildTreasureEnlarge.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aktualny poziom&8: {c}"+(guild.getTreasure().getLevel()+1), " ", "&8>> {n}Nie posiadasz uprawnienia!")));
        }
        gui.setItem(1, memberManagement.toItemStack());
        gui.setItem(3, guildResources.toItemStack());
        gui.setItem(5, guildTreasure.toItemStack());
        gui.setItem(7, guildTimeRenew.toItemStack());
        gui.setItem(19, guildRegionEnlarge.toItemStack());
        gui.setItem(21, guildTreasureEnlarge.toItemStack());
        gui.setItem(23, guildMembersEnlarge.toItemStack());
        gui.setItem(25, guildAlliancesEnlarge.toItemStack());
        gui.setItem(10, color.clone());
        gui.setItem(12, color.clone());
        gui.setItem(14, color.clone());
        gui.setItem(16, color.clone());
        gui.setEmptyItem(blank.clone());
        return gui;
    }
}
