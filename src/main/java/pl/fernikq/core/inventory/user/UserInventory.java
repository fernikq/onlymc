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
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.user.*;
import pl.fernikq.core.inventory.enums.user.*;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.kit.KitItem;
import pl.fernikq.core.shop.Shop;
import pl.fernikq.core.shop.ShopItem;
import pl.fernikq.core.shop.ShopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.NumberUtil;

import java.util.Arrays;
import java.util.Map;

public class UserInventory {

    private final CorePlugin plugin;
    private ItemStack blank;
    private ItemStack color;
    private ItemStack backGlass;
    private ItemStack backBarrier;

    public UserInventory(CorePlugin plugin){
        this.plugin = plugin;
        blank = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).setName(" ").toItemStack();
        color = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(" ").toItemStack();
        backGlass = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        backBarrier = new ItemBuilder(new ItemStack(Material.BARRIER, 1)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
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
        gui.setItem(29, turboSystem.toItemStack());
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
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Szansa&8: {c}"+ NumberUtil.formatDouble(drop.getChance())+"%", "&8>> {n}Wypada w ilosci&8: {c}"+(drop.getMinAmount() == drop.getMaxAmount() ? drop.getMinAmount() : drop.getMinAmount()+"&8-{c}"+drop.getMaxAmount()),
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
        gui.setItem(43, new ItemBuilder(Material.COBBLESTONE).setName(ChatUtil.fixColor("&f&lDrop Cobblestone'a")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Kliknij aby "+(this.plugin.getDropManager().getDisabledCobblestone().contains(user) ? "&awlaczyc" : "&cwylaczyc")))).toItemStack(), new DropAction(this.plugin, DropActionType.CHANGE_COBBLESTONE_STATUS, user));
        gui.setItem(44, new ItemBuilder(Material.PAPER).setName(ChatUtil.fixColor("&f&lWiadomosci")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
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
        ItemBuilder premiumCaseItem = new ItemBuilder(this.plugin.getDropManager().getPremiumCaseItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lOtworzone "+this.plugin.getDropManager().getPremiumCaseNameInGUI())).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otworzyles&8: {c}"+user.getUserStat().getOpenedPremiumCase())));
        ItemBuilder cobblexItem = new ItemBuilder(this.plugin.getDropManager().getCobblexItem().clone().getType()).setName(ChatUtil.fixColor("{c}&lOtworzone "+this.plugin.getDropManager().getCobblexNameInGUI())).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otworzyles&8: {c}"+user.getUserStat().getOpenedCobblex())));
        ItemBuilder stoneItem = new ItemBuilder(Material.STONE).setName(ChatUtil.fixColor("{c}&lWykopany kamien")).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getLevel()+" {n}poziom","&8>> {n}Wykopales&8: {c}"+user.getUserStat().getMinedStone())));
        ItemBuilder coinsFromStoneItem = new ItemBuilder(Material.DOUBLE_PLANT).setName(ChatUtil.fixColor("{c}&lMonety za kopanie")).
                setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otrzymales&8: {c}"+user.getUserStat().getCoinsFromStone())));
        gui.setItem(0, stoneItem.toItemStack());
        gui.setItem(2, premiumCaseItem.toItemStack());
        gui.setItem(6, cobblexItem.toItemStack());
        gui.setItem(8, coinsFromStoneItem.toItemStack());
        gui.setItem(4, this.backGlass, new DropAction(this.plugin, DropActionType.BACK_TO_MENU, user));
        gui.setEmptyItem(this.blank);
        user.addInventory(gui);
        return gui;
    }
}
