package pl.fernikq.core.inventory.user;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.drop.Drop;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.QuestAction;
import pl.fernikq.core.inventory.actions.TopsAction;
import pl.fernikq.core.inventory.actions.user.*;
import pl.fernikq.core.inventory.enums.QuestActionType;
import pl.fernikq.core.inventory.enums.TopsActionType;
import pl.fernikq.core.inventory.enums.user.*;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.kit.KitItem;
import pl.fernikq.core.shop.Shop;
import pl.fernikq.core.shop.ShopItem;
import pl.fernikq.core.shop.ShopType;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.UserStat;
import pl.fernikq.core.user.quests.Quest;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserInventory {

    private final CorePlugin plugin;
    private ItemStack blank;
    private ItemStack color;
    private ItemStack backGlass;
    private ItemStack backBarrier;
    private String customHeadName;

    public UserInventory(CorePlugin plugin){
        this.plugin = plugin;
        blank = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).setName(" ").toItemStack();
        color = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(" ").toItemStack();
        backGlass = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        backBarrier = new ItemBuilder(new ItemStack(Material.BARRIER, 1)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        customHeadName = "King";
    }

    public InventoryGUI kitMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDostepne zestawy &8]", 1, true);
        user.addInventory(gui);
        for(Kit kit : this.plugin.getKitManager().getKits()){
            ItemBuilder builder = new ItemBuilder(kit.getItem().clone()).setAmount(1).setName(ChatUtil.fixColor(kit.getName()));
            builder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Zestaw dostepny dla "+(kit.getGroup().equals(UserGroup.PLAYER) ? "&7Gracza" : kit.getGroup().getPrefix()), "&8>> {n}Kliknij aby obejrzec")));
            gui.addItem(builder.toItemStack(), new KitAction(this.plugin, kit, KitActionType.CHOOSE, user));
        }
        gui.setEmptyItem(blank);
        return gui;
    }

    public InventoryGUI kit(User user, Kit kit){
        InventoryGUI gui = new InventoryGUI(kit.getName(), 6, true);
        user.addInventory(gui);
        for(KitItem kitItem : kit.getItems()){
            ItemBuilder builder = new ItemBuilder(kitItem.getItemStack().clone()).setAmount(1).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ilosc&8: {c}"+kitItem.getItemStack().getAmount())));
            if(kitItem.getName() != null){
                builder.setName(ChatUtil.fixColor("{c}&l"+kitItem.getName()));
            }
            gui.addItem(builder.toItemStack());
        }
        ItemBuilder take = new ItemBuilder(Material.WOOL).setDurability((short)5).setName(ChatUtil.fixColor("&a&lOdbierz zestaw"));
        take.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Czas&8: "+(this.plugin.getKitManager().canTakeByTime(user, kit) ? "&aTak" : "&cNie"), "&8>> {n}Ranga&8: "+(this.plugin.getKitManager().canTakeByGroup(user, kit) ? "&aTak" : "&cNie"))));
        ItemBuilder back = new ItemBuilder(Material.WOOL).setDurability((short)14).setName(ChatUtil.fixColor("&c&lPowrot"));
        gui.setItem(52, back.toItemStack(), new KitAction(this.plugin, KitActionType.BACK, user));
        gui.setItem(53, take.toItemStack(), new KitAction(this.plugin, kit, KitActionType.TAKE, user));
        return gui;
    }

    public InventoryGUI craftingsMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDostepne craftingi &8]", 1, true);
        user.addInventory(gui);
        for(Generator generator : this.plugin.getGeneratorManager().getGenerators()){
            ItemBuilder builder = new ItemBuilder(generator.getItemStack().clone()).setAmount(1);
            builder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc crafting")));
            gui.addItem(builder.toItemStack(), new CraftingAction(plugin, CraftingActionType.CHOOSE, generator, user));
        }
        return gui;
    }

    public InventoryGUI craftings(User user, Generator generator){
        InventoryGUI gui = new InventoryGUI(generator.getInventoryName(), 5, true);
        user.addInventory(gui);
        int index = 0;
        for(int i = 10; i < 13; i++){
            String ingredientInfo = generator.getIngredients().get(index);
            gui.setItem(i, new ItemStack(ItemUtil.getMaterial(ingredientInfo), 1, (short) 0));
            index++;
        }
        for(int i = 19; i < 22; i++){
            String ingredientInfo = generator.getIngredients().get(index);
            gui.setItem(i, new ItemStack(ItemUtil.getMaterial(ingredientInfo), 1, (short) 0));
            index++;
        }
        for(int i = 28; i < 31; i++){
            String ingredientInfo = generator.getIngredients().get(index);
            gui.setItem(i, new ItemStack(ItemUtil.getMaterial(ingredientInfo), 1, (short) 0));
            index++;
        }
        gui.setItem(24, generator.getItemStack().clone());
        gui.setItem(43, backBarrier, new CraftingAction(plugin, CraftingActionType.BACK, user));
        ItemBuilder autoCraft = new ItemBuilder(Material.WORKBENCH).setName(ChatUtil.fixColor("&a&lAutomatyczny crafting"));
        if(generator.hasItems(user.asPlayer())){
            autoCraft.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&aPosiadasz przedmioty!")));
        }else {
            autoCraft.addLore(" ");
            for(Map.Entry<Material, Integer> item : generator.getAmounts().entrySet()) {
                autoCraft.addLore(ChatUtil.fixColor("&8>> {n}" + item.getKey().name().toLowerCase() + "&8: {c}" + ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), item.getKey(), (short) 0) + "&8/{c}" + item.getValue()));
            }
        }
        gui.setItem(44, autoCraft.toItemStack(), new CraftingAction(plugin, CraftingActionType.CRAFT, generator, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI shopMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSKLEP &8]", 1, true);
        user.addInventory(gui);
        gui.setItem(2, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 5).setName(ChatUtil.fixColor("&a&lKupno"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby kupic przedmioty&8!"))).toItemStack(), new ShopAction(this.plugin, ShopActionType.CHOOSE_BUY, user));
        gui.setItem(6, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 14).setName(ChatUtil.fixColor("&c&lSprzedaz"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby sprzedac przedmioty&8!"))).toItemStack(), new ShopAction(this.plugin, ShopActionType.CHOOSE_SELL, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI shopBuyMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lKUPNO - RODZAJ &8]", 2, true);
        user.addInventory(gui);
        for(Shop shop : this.plugin.getShopManager().getShops(ShopType.BUY)){
            gui.addItem(shop.getItem().clone(), new ShopAction(this.plugin, shop, ShopActionType.CHOOSE_BUY_TYPE, user));
        }
        gui.setItem(17, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_MENU, user));
        return gui;
    }

    public InventoryGUI shopBuy(User user, Shop shop){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lKUPOWANIE &8]", 6, true);
        user.addInventory(gui);
        for(ShopItem shopItem : shop.getItems()){
            ItemBuilder itemBuilder = new ItemBuilder(shopItem.getItemStack().clone());
            if(shopItem.getName() != null){
                itemBuilder.setName(ChatUtil.fixColor(shopItem.getName()));
            }
            itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ilosc&8: {c}"+shopItem.getAmount(),
                    "&8>> {n}Cena&8: {c}"+shopItem.getPrice())));
            gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, shop, ShopActionType.BUY_ITEM, user));
        }
        gui.setItem(53, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_BUY_MENU, user));
        gui.setEmptyItem(new ItemBuilder(this.blank.clone()).setName(ChatUtil.fixColor("&8>> {n}Posiadasz {c}"+user.getUserStat().getCoins()+" {n}monet")).toItemStack());
        return gui;
    }

    public InventoryGUI levelShopBuy(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSKLEP ZA LEVEL &8]", 5, true);
        user.addInventory(gui);
        for(Shop shop : this.plugin.getShopManager().getShops(ShopType.LEVEL)) {
            for(ShopItem shopItem : shop.getItems()) {
                ItemBuilder itemBuilder = new ItemBuilder(shopItem.getItemStack().clone());
                if(shopItem.getName() != null) {
                    itemBuilder.setName(ChatUtil.fixColor(shopItem.getName()));
                }
                itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ilosc&8: {c}" + shopItem.getAmount(), "&8>> {n}Cena&8: {c}" + shopItem.getPrice() + " {n}LVL", "&8>> {n}Wymagany poziom&8: {c}" + (shopItem.getPrice() + 1))));
                gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, shop, ShopActionType.BUY_FROM_LEVEL_SHOP, user));
            }
        }
        gui.setItem(44, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        gui.setEmptyItem(new ItemBuilder(this.blank.clone()).setName(ChatUtil.fixColor("&8>> {n}Posiadasz {c}"+user.getUserStat().getLevel()+" {n}poziom gornictwa")).toItemStack());
        return gui;
    }

    public InventoryGUI shopSellMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSPRZEDAZ - RODZAJ &8]", 2, true);
        user.addInventory(gui);
        for(Shop shop : this.plugin.getShopManager().getShops(ShopType.SELL)){
            gui.addItem(shop.getItem().clone(), new ShopAction(this.plugin, shop, ShopActionType.CHOOSE_SELL_TYPE, user));
        }
        gui.setItem(17, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_MENU, user));
        return gui;
    }

    public InventoryGUI shopSell(User user, Shop shop){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSPRZEDAWANIE&8]", 6, true);
        user.addInventory(gui);
        for(ShopItem shopItem : shop.getItems()){
            ItemBuilder itemBuilder = new ItemBuilder(shopItem.getItemStack().clone());
            if(shopItem.getName() != null){
                itemBuilder.setName(ChatUtil.fixColor(shopItem.getName()));
            }
            itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ilosc&8: {c}"+shopItem.getAmount(),
                    "&8>> {n}Cena&8: {c}"+shopItem.getPrice())));
            gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, shop, ShopActionType.SELL_ITEM, user));
        }
        gui.setItem(53, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_SELL_MENU, user));
        gui.setEmptyItem(new ItemBuilder(this.blank.clone()).setName(ChatUtil.fixColor("&8>> {n}Posiadasz {c}"+user.getUserStat().getCoins()+" {n}monet")).toItemStack());
        return gui;
    }

    public InventoryGUI deposite(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSCHOWEK &8]", 1, true);
        user.addInventory(gui);
        ItemBuilder apples = new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatUtil.fixColor("&8[ {c}&lRefile &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Limit&8: {c}" + ConfigManager.maxGoldenApplesInInventory,
                        "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getDepositeApples())));
        ItemBuilder enchantedApples = new ItemBuilder(Material.GOLDEN_APPLE).setDurability((short) 1).setName(ChatUtil.fixColor("&8[ {c}&lKoxy &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Limit&8: {c}" + ConfigManager.maxEnchantedGoldenApplesInInventory,
                        "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getDepositeEnchantedApples())));
        ItemBuilder pearls = new ItemBuilder(Material.ENDER_PEARL).setName(ChatUtil.fixColor("&8[ {c}&lPerly &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Limit&8: {c}" + ConfigManager.maxPearlsInInventory,
                        "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getDepositePearls())));
        ItemBuilder all = new ItemBuilder(Material.HOPPER).setName(ChatUtil.fixColor("&8[ {c}&lWszystko &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby wyplacic wszystko&8!")));
        gui.setItem(1, enchantedApples.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_ENCHANTED_APPLES, user));
        gui.setItem(3, apples.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_APPLES, user));
        gui.setItem(5, pearls.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_PEARLS, user));
        gui.setItem(7, all.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_ALL, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI dropMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lMenu dropu &8]", 5, true);
        user.addInventory(gui);
        ItemBuilder stoneDrop = new ItemBuilder(Material.STONE).setName(ChatUtil.fixColor("{c}&lDrop z kamienia")).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder caseDrop = new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lDrop z "+this.plugin.getDropManager().getPremiumCaseNameInGUI())).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder cobblexDrop = new ItemBuilder(this.plugin.getDropManager().getCobblexItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lDrop z "+this.plugin.getDropManager().getCobblexNameInGUI())).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder turboSystem = new ItemBuilder(Material.GOLD_PICKAXE).setName(ChatUtil.fixColor("{c}&lSystem Turbo")).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder statistics = new ItemBuilder(Material.PAPER).setName(ChatUtil.fixColor("{c}&lStatystyki")).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder levelShop = new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lSklep za poziom")).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        gui.setItem(10, stoneDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_STONE_DROP, user));
        gui.setItem(13, caseDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_PREMIUMCASE_DROP, user));
        gui.setItem(16, cobblexDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_COBBLEX_DROP, user));
        gui.setItem(29, turboSystem.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_TURBO_SYSTEM, user));
        gui.setItem(31, levelShop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_LEVEL_SHOP, user));
        gui.setItem(33, statistics.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_STATISTICS, user));
        gui.setItem(1, this.color);
        gui.setItem(4, this.color);
        gui.setItem(7, this.color);
        gui.setItem(19, this.color);
        gui.setItem(22, this.color);
        gui.setItem(25, this.color);
        gui.setItem(38, this.color);
        gui.setItem(40, this.color);
        gui.setItem(42, this.color);
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI dropStone(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDrop z kamienia &8]", 5, true);
        user.addInventory(gui);
        for(Drop drop : this.plugin.getDropManager().getDrops(DropType.STONE)){
            ItemBuilder dropItem = new ItemBuilder(drop.getItemStack().clone());
            if(drop.getName() != null){
                dropItem.setName(ChatUtil.fixColor(drop.getName()));
            }
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Szansa&8: {c}"+(user.getUserStat().isTurboDrop() ? (NumberUtil.formatDouble(drop.getChance() * ConfigManager.turboDropMultiplier)+"% &8[{c}&lTURBO&8]") : drop.getChance()+"%"), "&8>> {n}Wypada w ilosci&8: {c}"+(drop.getMinAmount() == drop.getMaxAmount() ? drop.getMinAmount() : drop.getMinAmount()+"&8-{c}"+drop.getMaxAmount()),
                    "&8>> {n}Wypada ponizej {c}"+drop.getMinY()+" {n}kratki", "&8>> {n}Fortuna&8: "+(drop.isFortune() ? "&aTak" : "&cNie"), " ", "&8>> {n}Aktywny&8: "+(drop.getDisabled().contains(user) ? "&cNie" : "&aTak"))));
            if(!drop.getDisabled().contains(user)){
                dropItem.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10);
                dropItem.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            }
            gui.addItem(dropItem.toItemStack(), new DropAction(this.plugin, drop, DropActionType.CHANGE_ONE_DROP, user));
        }
        gui.setItem(36, new ItemBuilder(Material.WOOL).setDurability((short) 5).setName(ChatUtil.fixColor("&a&lWlacz drop")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Kliknij aby wszystko &awlaczyc&8!"))).toItemStack(), new DropAction(this.plugin, DropActionType.ON_ALL_DROPS, user));
        gui.setItem(37, new ItemBuilder(Material.WOOL).setDurability((short) 14).setName(ChatUtil.fixColor("&c&lWylacz drop")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Kliknij aby wszystko &cwylaczyc&8!"))).toItemStack(), new DropAction(this.plugin, DropActionType.OFF_ALL_DROPS, user));
        gui.setItem(43, new ItemBuilder(Material.COBBLESTONE).setName(ChatUtil.fixColor("{n}&lDrop Cobblestone'a")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Kliknij aby "+(this.plugin.getDropManager().getDisabledCobblestone().contains(user) ? "&awlaczyc" : "&cwylaczyc")))).toItemStack(), new DropAction(this.plugin, DropActionType.CHANGE_COBBLESTONE_STATUS, user));
        gui.setItem(44, new ItemBuilder(Material.PAPER).setName(ChatUtil.fixColor("{n}&lWiadomosci")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Kliknij aby "+(user.getUserChat().isDropMessages() ? "&cwylaczyc" : "&awlaczyc")))).toItemStack(), new DropAction(this.plugin, DropActionType.CHANGE_MESSAGES_STATUS, user));
        gui.setItem(40, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        gui.setItem(41, this.blank);
        gui.setItem(42, this.blank);
        gui.setItem(39, this.blank);
        gui.setItem(38, this.blank);
        return gui;
    }

    public InventoryGUI dropPremiumCase(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDrop z "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+" &8]", 5, true);
        user.addInventory(gui);
        for(Drop drop : this.plugin.getDropManager().getDrops(DropType.PREMIUMCASE)){
            ItemBuilder dropItem = new ItemBuilder(drop.getItemStack().clone());
            if(drop.getName() != null){
                dropItem.setName(ChatUtil.fixColor(drop.getName()));
            }
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Szansa&8: {c}"+drop.getChance()+"%", "&8>> {n}Wypada w ilosci&8: {c}"+(drop.getMinAmount() == drop.getMaxAmount() ? drop.getMinAmount() : drop.getMinAmount()+"&8-{c}"+drop.getMaxAmount()))));
            gui.addItem(dropItem.toItemStack());
        }
        gui.setEmptyItem(this.blank);
        gui.setItem(44, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        return gui;
    }

    public InventoryGUI dropCobblex(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDrop z "+this.plugin.getDropManager().getCobblexNameInGUI()+" &8]", 5, true);
        user.addInventory(gui);
        for(Drop drop : this.plugin.getDropManager().getDrops(DropType.COBBLEX)){
            ItemBuilder dropItem = new ItemBuilder(drop.getItemStack().clone());
            if(drop.getName() != null){
                dropItem.setName(ChatUtil.fixColor(drop.getName()));
            }
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Wypada w ilosci&8: {c}"+(drop.getMinAmount() == drop.getMaxAmount() ? drop.getMinAmount() : drop.getMinAmount()+"&8-{c}"+drop.getMaxAmount()))));
            gui.addItem(dropItem.toItemStack());
        }
        gui.setEmptyItem(this.blank);
        gui.setItem(44, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        return gui;
    }

    public InventoryGUI dropStatistics(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lStatystyki &8]", 1, true);
        user.addInventory(gui);
        ItemBuilder premiumCaseItem = new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lOtworzone "+this.plugin.getDropManager().getPremiumCaseNameInGUI())).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otworzyles&8: {c}"+user.getUserStat().getOpenedPremiumCase())));
        ItemBuilder cobblexItem = new ItemBuilder(this.plugin.getDropManager().getCobblexItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lOtworzone "+this.plugin.getDropManager().getCobblexNameInGUI())).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otworzyles&8: {c}"+user.getUserStat().getOpenedCobblex())));
        ItemBuilder stoneItem = new ItemBuilder(Material.STONE).setName(ChatUtil.fixColor("{c}&lWykopany kamien")).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getLevel()+" {n}poziom","&8>> {n}Wykopales&8: {c}"+user.getUserStat().getMinedStone()+" {n}kamienia")));
        ItemBuilder coinsFromStoneItem = new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lMonety za kopanie")).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otrzymales&8: {c}"+user.getUserStat().getCoinsFromStone())));
        gui.setItem(0, stoneItem.toItemStack());
        gui.setItem(2, premiumCaseItem.toItemStack());
        gui.setItem(6, cobblexItem.toItemStack());
        gui.setItem(8, coinsFromStoneItem.toItemStack());
        gui.setItem(4, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI dropTurboSystem(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSystem Turbo &8]", 1, true);
        ItemBuilder turboDrop = new ItemBuilder(Material.GOLD_PICKAXE).setName(ChatUtil.fixColor("{c}&lTurboDrop")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {c}&lSerwer &8<<", " ")));
        turboDrop.addLore(ChatUtil.fixColor("&8>> {n}Aktywny&8: "+(ConfigManager.turboDropTime > System.currentTimeMillis() ? "&aTak" : "&cNie")));
        if(ConfigManager.turboDropTime > System.currentTimeMillis()){
            turboDrop.addLore(ChatUtil.fixColor("&8>> {n}Wygasa za&8: {c}"+ TimeUtil.getTimeToString(ConfigManager.turboDropTime - System.currentTimeMillis())));
        }
        turboDrop.addLore(" ");
        turboDrop.addLore(ChatUtil.fixColor("&8>> {c}&lGracz &8<<"));
        turboDrop.addLore(" ");
        turboDrop.addLore(ChatUtil.fixColor("&8>> {n}Aktywny&8: "+(user.getUserStat().getTurboDropTime() > System.currentTimeMillis() ? "&aTak" : "&cNie")));
        if(user.getUserStat().getTurboDropTime() > System.currentTimeMillis()){
            turboDrop.addLore(ChatUtil.fixColor("&8>> {n}Wygasa za&8: {c}"+ TimeUtil.getTimeToString(user.getUserStat().getTurboDropTime() - System.currentTimeMillis())));
        }

        ItemBuilder turboExp = new ItemBuilder(Material.EXP_BOTTLE).setName(ChatUtil.fixColor("{c}&lTurboExp")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {c}&lSerwer &8<<", " ")));
        turboExp.addLore(ChatUtil.fixColor("&8>> {n}Aktywny&8: "+(ConfigManager.turboExpTime > System.currentTimeMillis() ? "&aTak" : "&cNie")));
        if(ConfigManager.turboExpTime > System.currentTimeMillis()){
            turboExp.addLore(ChatUtil.fixColor("&8>> {n}Wygasa za&8: {c}"+ TimeUtil.getTimeToString(ConfigManager.turboExpTime - System.currentTimeMillis())));
        }
        turboExp.addLore(" ");
        turboExp.addLore(ChatUtil.fixColor("&8>> {c}&lGracz &8<<"));
        turboExp.addLore(" ");
        turboExp.addLore(ChatUtil.fixColor("&8>> {n}Aktywny&8: "+(user.getUserStat().getTurboExpTime() > System.currentTimeMillis() ? "&aTak" : "&cNie")));
        if(user.getUserStat().getTurboExpTime() > System.currentTimeMillis()){
            turboExp.addLore(ChatUtil.fixColor("&8>> {n}Wygasa za&8: {c}"+ TimeUtil.getTimeToString(user.getUserStat().getTurboExpTime() - System.currentTimeMillis())));
        }
        gui.setItem(4, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        gui.setItem(1, turboDrop.toItemStack());
        gui.setItem(7, turboExp.toItemStack());
        gui.setEmptyItem(this.blank);
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI enderchestUpgrade(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lUlepszenia endera &8]", 3, true);
        user.addInventory(gui);
        ItemBuilder ender1 = new ItemBuilder(Material.ENDER_CHEST).setName(ChatUtil.fixColor("{n}Poziom {c}1"));
        ItemBuilder ender2 = new ItemBuilder(Material.ENDER_CHEST).setName(ChatUtil.fixColor("{n}Poziom {c}2"));
        ItemBuilder ender3 = new ItemBuilder(Material.ENDER_CHEST).setName(ChatUtil.fixColor("{n}Poziom {c}3"));
        if(user.getEnderchest().getLevel() >= 3){
            ender1.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}maksymalny {n}poziom enderchesta")));
            ender2.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}maksymalny {n}poziom enderchesta")));
            ender3.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}maksymalny {n}poziom enderchesta")));
        }else{
            if(user.getEnderchest().getLevel() == 2){
                ender1.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}wyzszy {n}poziom enderchesta")));
                ender2.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}podany {n}poziom enderchesta")));
                ender3.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby ulepszyc do {c}"+3+" {n}poziomu", " ", "&8>> {n}Koszt&8: {c}"+user.getEnderchest().getCostByLevel())));
            }
            if(user.getEnderchest().getLevel() == 1){
                ender1.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz {c}podany {n}poziom enderchesta")));
                ender2.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby ulepszyc do {c}2 {n}poziomu", " ", "&8>> {n}Koszt&8: {c}"+user.getEnderchest().getCostByLevel())));
                ender3.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aby ulepszyc enderchest do poziomu {c}3", "&8>> {n}musisz posiadac {c}2 {n}poziom enderchesta")));
            }
            if(user.getEnderchest().getLevel() == 0){
                ender1.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby ulepszyc do {c}1 {n}poziomu", " ", "&8>> {n}Koszt&8: {c}"+user.getEnderchest().getCostByLevel())));
                ender2.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aby ulepszyc enderchest do poziomu {c}2", "&8>> {n}musisz posiadac {c}1 {n}poziom enderchesta")));
                ender3.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Aby ulepszyc enderchest do poziomu {c}3", "&8>> {n}musisz posiadac {c}2 {n}poziom enderchesta")));
            }
        }
        gui.setItem(10, ender1.toItemStack(), new EnderchestUpgradeAction(this.plugin, 1, user));
        gui.setItem(13, ender2.toItemStack(), new EnderchestUpgradeAction(this.plugin, 2, user));
        gui.setItem(16, ender3.toItemStack(), new EnderchestUpgradeAction(this.plugin, 3, user));
        gui.setItem(1, this.color.clone());
        gui.setItem(19, this.color.clone());
        gui.setItem(4, this.color.clone());
        gui.setItem(22, this.color.clone());
        gui.setItem(7, this.color.clone());
        gui.setItem(25, this.color.clone());
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI chatSettings(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lUstawienia chatu &8]", 3, true);
        user.addInventory(gui);
        ItemBuilder guildMessages = new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{n}Wiadomosc zwiazane z gildiami"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Status&8: "+(user.getUserChat().isGuildMessages() ? "&awlaczone" : "&cwylaczone"))));;
        ItemBuilder fightMessages = new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatUtil.fixColor("{n}Wiadomosci zwiazane z walka"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Status&8: "+(user.getUserChat().isFightMessages() ? "&awlaczone" : "&cwylaczone"))));;
        ItemBuilder autoMessages = new ItemBuilder(Material.PAPER).setName(ChatUtil.fixColor("{n}Automatyczne wiadomosci"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Status&8: "+(user.getUserChat().isAutoMessages() ? "&awlaczone" : "&cwylaczone"))));;
        ItemBuilder caseMessages = new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().getType()).setName(ChatUtil.fixColor("{n}Wiadomosci z "+this.plugin.getDropManager().getPremiumCaseNameInGUI()))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Status&8: "+(user.getUserChat().isPremiumCaseMessages() ? "&awlaczone" : "&cwylaczone"))));
        gui.setItem(10, guildMessages.toItemStack(), new ChatSettingsAction(this.plugin, ChatSettingsActionType.GUILD, user));
        gui.setItem(12, fightMessages.toItemStack(), new ChatSettingsAction(this.plugin, ChatSettingsActionType.FIGHT, user));
        gui.setItem(14, autoMessages.toItemStack(), new ChatSettingsAction(this.plugin, ChatSettingsActionType.AUTOMESSAGE, user));
        gui.setItem(16, caseMessages.toItemStack(), new ChatSettingsAction(this.plugin, ChatSettingsActionType.CASE, user));
        gui.setItem(1, this.color.clone());
        gui.setItem(19, this.color.clone());
        gui.setItem(3, this.color.clone());
        gui.setItem(21, this.color.clone());
        gui.setItem(5, this.color.clone());
        gui.setItem(23, this.color.clone());
        gui.setItem(7, this.color.clone());
        gui.setItem(25, this.color.clone());
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI playerInfo(User user, User target) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lInformacje o graczu &8]", 5, true);
        user.addInventory(gui);
        gui.setItem(1, this.color);
        gui.setItem(3, this.color);
        gui.setItem(5, this.color);
        gui.setItem(7, this.color);
        gui.setItem(13, this.color);
        gui.setItem(19, this.color);
        gui.setItem(21, this.color);
        gui.setItem(23, this.color);
        gui.setItem(25, this.color);
        gui.setItem(31, this.color);
        gui.setItem(37, this.color);
        gui.setItem(39, this.color);
        gui.setItem(41, this.color);
        gui.setItem(43, this.color);
        UserStat stat = target.getUserStat();
        ItemBuilder playerName = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(target.getName()).setName(ChatUtil.fixColor("{n}Nick&8: {c}"+target.getName()));
        ItemBuilder fight = new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatUtil.fixColor("{c}&lWalka")).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Zabojstwa&8: {c}"+stat.getKills(), "&8>> {n}Smierci&8: {c}"+stat.getDeaths(),
                        "&8>> {n}Punkty&8: {c}"+stat.getPoints(), "&8>> {n}Logouty&8: {c}"+stat.getLogouts(), "&8>> {n}Asysty&8: {c}"+stat.getAssists())));
        ItemBuilder rank = new ItemBuilder(Material.BOOK).setName(ChatUtil.fixColor("{c}&lRanga")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiada range {c}"+(target.getGroup().equals(UserGroup.PLAYER) ? "Gracz" : target.getGroup().name()))));
        ItemBuilder turbo = new ItemBuilder(Material.GOLD_PICKAXE).setName(ChatUtil.fixColor("{c}&lTurbo")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}TurboDrop&8: "+(stat.isTurboDrop() ? "&aTak" : "&cNie"), "&8>> {n}TurboExp&8: "+(stat.isTurboExp() ? "&aTak" : "&cNie"))));
        ItemBuilder guildItem = new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{c}&lGildia"));
        if(target.hasGuild()){
            Guild guild = target.getGuild();
            guildItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Tag&8: {c}"+guild.getTag(), "&8>> {n}Nazwa&8: {c}"+guild.getName(),
                    "&8>> {n}Zalozyciel&8: {c}"+guild.getOwner().getName(), "&8>> {n}Punkty&8: {c}"+guild.getPoints(), "&8>> {n}Zabojstwa&8: {c}"+guild.getKills(), "&8>> {n}Smierci&8: {c}"+guild.getDeaths(),
                    "&8>> {n}Asysty&8: {c}"+guild.getAssists())));
        }else{
            guildItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nie posiada gildii!")));
        }
        ItemBuilder drop = new ItemBuilder(Material.STONE).setName(ChatUtil.fixColor("{c}&lDrop")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Wykopany kamien&8: {c}"+stat.getMinedStone(), "&8>> {n}Poziom&8: {c}"+stat.getLevel(), "&8>> {n}Otworzone "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"&8: {c}"+stat.getOpenedPremiumCase(),
                "&8>> {n}Otworzone "+this.plugin.getDropManager().getCobblexNameInGUI()+"&8: {c}"+stat.getOpenedCobblex())));
        ItemBuilder coins = new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lMonety")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiada&8: {c}"+stat.getCoins()+" {n}monet", "&8>> {n}Wykopanych z kamienia&8: {c}"+stat.getCoinsFromStone())));
        ItemBuilder distance = new ItemBuilder(Material.GOLD_BOOTS).setName(ChatUtil.fixColor("{c}&lDystans")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Przebyty dystans&8: {c}"+stat.getDistanceTraveled()+"m")));
        ItemBuilder online = new ItemBuilder(Material.WATCH).setName(ChatUtil.fixColor("{c}&lCzas Online")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                 "&8>> {n}Online&8: "+(target.asPlayer() != null ? "&aTak" : "&cNie"), "&8>> {n}Spedzil&8: {c}"+TimeUtil.getTimeToString(stat.getOnlineTime()))));
        gui.setItem(22, playerName.toItemStack());
        gui.setItem(10, fight.toItemStack());
        gui.setItem(12, rank.toItemStack());
        gui.setItem(14, turbo.toItemStack());
        gui.setItem(16, guildItem.toItemStack());
        gui.setItem(28, drop.toItemStack());
        gui.setItem(30, coins.toItemStack());
        gui.setItem(32, distance.toItemStack());
        gui.setItem(34, online.toItemStack());
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI topsMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lMENU TOPEK &8]", 1, true);
        user.addInventory(gui);
        gui.setItem(2, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner("Notch").setName(ChatUtil.fixColor("{c}&lTopki graczy")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc"))).toItemStack(), new TopsAction(this.plugin, null, TopsActionType.OPEN_USER_TOPS_SELECT, user));
        gui.setItem(6, new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{c}&lTopki gildii")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc"))).toItemStack(), new TopsAction(this.plugin, null, TopsActionType.OPEN_GUILD_TOPS_SELECT, user));
        gui.setItem(1, this.color);
        gui.setItem(3, this.color);
        gui.setItem(5, this.color);
        gui.setItem(7, this.color);
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI playerTops(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lTOPKI - WYBOR &8]", 3, true);
        user.addInventory(gui);
        this.plugin.getTopManager().getTopsByKind(TopKind.USER).forEach(sortable -> {
            Material material = sortable.getTopType().getMaterial();
            String name = sortable.getTopType().getName();
            if(sortable.getTopType().equals(TopType.USER_COBBLEX)){
                material = this.plugin.getDropManager().getCobblexItem().getType();
                name = "Topka "+this.plugin.getDropManager().cobblexNameInGUI;
            }
            if(sortable.getTopType().equals(TopType.USER_CASE)){
                material = this.plugin.getDropManager().getPremiumCaseItem().getType();
                name = "Topka "+this.plugin.getDropManager().premiumCaseNameInGUI;
            }
            gui.addItem(new ItemBuilder(material).setName(ChatUtil.fixColor("{c}&l"+name)).toItemStack(), new TopsAction(this.plugin, sortable, TopsActionType.CHOOSE_USER_TOP, user));
        });
        gui.setItem(26, this.backGlass.clone(), new TopsAction(this.plugin, null, TopsActionType.BACK_TO_MAIN_MENU, user));
        return gui;
    }

    public InventoryGUI playerTop(User user, Sortable<User> top){
        InventoryGUI inventoryGUI = top.getInventory(user);
        inventoryGUI.setItem(48, this.backGlass, new TopsAction(this.plugin, top, TopsActionType.OPEN_USER_TOPS_SELECT, user));
        inventoryGUI.setItem(50, this.backGlass, new TopsAction(this.plugin, top, TopsActionType.OPEN_USER_TOPS_SELECT, user));
        inventoryGUI.setEmptyItem(this.blank);
        user.addInventory(inventoryGUI);
        return inventoryGUI;
    }

    public InventoryGUI playerQuestsMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lZADANIA &8]", 3, true);
        this.plugin.getQuestManager().getQuestTypes().forEach(quest -> {
            gui.addItem(new ItemBuilder(quest.getMaterial()).setName(ChatUtil.fixColor("{c}&l"+quest.getName())).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc!"))).toItemStack(), new QuestAction(this.plugin, QuestActionType.OPEN_QUEST_GUI, quest, user));
        });
        gui.setEmptyItem(this.blank);
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI playerQuest(User user, QuestType questType) {
        InventoryGUI gui = new InventoryGUI("{c}&l"+questType.getName(), 3, true);
        gui.setEmptyItem(this.blank);
        gui.setItem(9, null);
        gui.setItem(11, null);
        gui.setItem(13, null);
        gui.setItem(15, null);
        gui.setItem(17, null);
        this.plugin.getQuestManager().getQuestsByType(questType).forEach(quest -> {
            ItemBuilder itemBuilder = new ItemBuilder(quest.getQuestType().getMaterial()).setName(ChatUtil.fixColor("{c}&l"+quest.getName()));
            if(this.plugin.getQuestManager().isDone(user, quest)){
                itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: &c&lWykonane")));
            }else{
                itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+this.plugin.getQuestManager().getAmountByQuest(user, questType)+"&8/{c}"+quest.getAmount())));
                if(questType.equals(QuestType.SPENT_TIME)){
                    itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+TimeUtil.getTimeToString(user.getUserStat().getOnlineTime())+" &8[&f"+ TimeUnit.MILLISECONDS.toHours(user.getUserStat().getOnlineTime())+"&8] " +"/{c} "+quest.getAmount()+"h")));
                }
                if(questType.equals(QuestType.COMEBACK)){
                    itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+user.getUserStat().getComebackDaysInRow()+"&8/{c}"+quest.getAmount())));
                }
            }
            gui.addItem(itemBuilder.toItemStack());
        });
        gui.setItem(26, this.backGlass, new QuestAction(this.plugin, QuestActionType.OPEN_MENU, null, user));
        user.addInventory(gui);
        return gui;
    }
}
