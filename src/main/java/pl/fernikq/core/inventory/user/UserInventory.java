package pl.fernikq.core.inventory.user;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.boss.BossDrop;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.customenchant.CustomEnchantItemEnum;
import pl.fernikq.core.drop.Drop;
import pl.fernikq.core.drop.DropType;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.QuestAction;
import pl.fernikq.core.inventory.actions.TopsAction;
import pl.fernikq.core.inventory.actions.user.*;
import pl.fernikq.core.inventory.actions.user.customenchant.CustomEnchantEnchantmentChoiceAction;
import pl.fernikq.core.inventory.actions.user.customenchant.CustomEnchantLevelChoiceAction;
import pl.fernikq.core.inventory.enums.QuestActionType;
import pl.fernikq.core.inventory.enums.TopsActionType;
import pl.fernikq.core.inventory.enums.user.*;
import pl.fernikq.core.kit.Kit;
import pl.fernikq.core.kit.KitItem;
import pl.fernikq.core.magiccase.MagicCaseDrop;
import pl.fernikq.core.magiccase.MagicCaseType;
import pl.fernikq.core.shop.Shop;
import pl.fernikq.core.shop.ShopItem;
import pl.fernikq.core.shop.ShopType;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.UserStat;
import pl.fernikq.core.user.backup.Backup;
import pl.fernikq.core.user.incognito.IncognitoType;
import pl.fernikq.core.user.incognito.UserIncognito;
import pl.fernikq.core.user.quests.QuestType;
import pl.fernikq.core.util.*;
import pl.nsclient.spigot.MCPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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
            if(!generator.isCanCraft()){
                continue;
            }
            ItemBuilder builder = new ItemBuilder(generator.getItemStack().clone()).setAmount(1);
            builder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc crafting")));
            gui.addItem(builder.toItemStack(), new CraftingAction(plugin, CraftingActionType.CHOOSE, generator, user));
        }
        gui.setEmptyItem(this.blank);
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
        gui.setItem(1, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 5).setName(ChatUtil.fixColor("&a&lKupno"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby kupic przedmioty&8!"))).toItemStack(), new ShopAction(this.plugin, ShopActionType.CHOOSE_BUY, user));
        gui.setItem(7, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 14).setName(ChatUtil.fixColor("&c&lSprzedaz"))
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
                    "&8>> {n}Cena&8: {c}"+shopItem.getPrice(), " ", "&eKliknij LPM + Shift", "&eaby sprzedac wszystko!")));
            gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, shop, ShopActionType.SELL_ITEM, user));
        }
        gui.setItem(53, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_SELL_MENU, user));
        gui.setEmptyItem(new ItemBuilder(this.blank.clone()).setName(ChatUtil.fixColor("&8>> {n}Posiadasz {c}"+user.getUserStat().getCoins()+" {n}monet")).toItemStack());
        return gui;
    }

    public InventoryGUI deposite(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSCHOWEK &8]", 3, true);
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
        ItemBuilder arrows = new ItemBuilder(Material.ARROW).setName(ChatUtil.fixColor("&8[ {c}&lStrzaly &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Limit&8: {c}" + ConfigManager.maxArrowsInInventory,
                        "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getDepositeArrows())));
        ItemBuilder snowballs = new ItemBuilder(Material.SNOW_BALL).setName(ChatUtil.fixColor("&8[ {c}&lSniezki &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Limit&8: {c}" + ConfigManager.maxSnowballsInInventory,
                        "&8>> {n}Posiadasz&8: {c}"+user.getUserStat().getDepositeSnowballs())));
        ItemBuilder all = new ItemBuilder(Material.HOPPER).setName(ChatUtil.fixColor("&8[ {c}&lWszystko &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby wyplacic wszystko&8!")));
        gui.setItem(10, enchantedApples.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_ENCHANTED_APPLES, user));
        gui.setItem(11, apples.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_APPLES, user));
        gui.setItem(15, pearls.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_PEARLS, user));
        gui.setItem(16, snowballs.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_SNOWBALLS, user));
        gui.setItem(13, arrows.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_ARROWS, user));
        gui.setItem(22, all.toItemStack(), new DepositeAction(this.plugin, DepositeActionType.TAKE_ALL, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI dropMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lMenu dropu &8]", 5, true);
        user.addInventory(gui);
        ItemBuilder stoneDrop = new ItemBuilder(Material.STONE).setName(ChatUtil.fixColor("{c}&lDrop z kamienia")).setLore(ChatUtil.fixColor(Arrays.asList(
                " ", "&8>> {n}Kliknij aby zobaczyc"
        )));
        ItemBuilder bossDrop = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 2).setName(ChatUtil.fixColor("{c}&lDrop z Bossa")).setLore(ChatUtil.fixColor(Arrays.asList(
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
        gui.setItem(12, caseDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_PREMIUMCASE_DROP, user));
        gui.setItem(14, bossDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_BOSS_DROP, user));
        gui.setItem(16, cobblexDrop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_COBBLEX_DROP, user));
        gui.setItem(29, turboSystem.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_TURBO_SYSTEM, user));
        gui.setItem(31, levelShop.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_LEVEL_SHOP, user));
        gui.setItem(33, statistics.toItemStack(), new DropAction(this.plugin, DropActionType.OPEN_STATISTICS, user));
        gui.setItem(1, this.color);
        gui.setItem(3, this.color);
        gui.setItem(5, this.color);
        gui.setItem(7, this.color);
        gui.setItem(19, this.color);
        gui.setItem(21, this.color);
        gui.setItem(23, this.color);
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
            double chance = drop.getChance();
            if(user.getUserStat().isTurboDrop()) chance *= ConfigManager.turboDropMultiplier;
            if(MCPlugin.getAuthorizedPlayers().contains(user.getName())) chance *= ConfigManager.turboDropCauseClientMultiplier;
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Szansa&8: {c}"+(user.getUserStat().isTurboDrop() ? (NumberUtil.formatDouble(chance)+"% &8[{c}&lTURBO&8]") : NumberUtil.formatDouble(chance)+"%"), "&8>> {n}Wypada w ilosci&8: {c}"+(drop.getMinAmount() == drop.getMaxAmount() ? drop.getMinAmount() : drop.getMinAmount()+"&8-{c}"+drop.getMaxAmount()),
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

    public InventoryGUI dropBoss(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDrop z Bossa &8]", 5, true);
        user.addInventory(gui);
        for(BossDrop bossDrop : this.plugin.getBossManager().getBossDrops()){
            ItemBuilder dropItem = new ItemBuilder(bossDrop.getItemStack().clone());
            if(bossDrop.getName() != null){
                dropItem.setName(ChatUtil.fixColor(bossDrop.getName()));
            }
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Szansa&8: {c}"+bossDrop.getChance()+"%", "&8>> {n}Wypada w ilosci&8: {c}"+(bossDrop.getMinAmount() == bossDrop.getMaxAmount() ? bossDrop.getMinAmount() : bossDrop.getMinAmount()+"&8-{c}"+bossDrop.getMaxAmount()))));
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
        ItemBuilder distance = new ItemBuilder(Material.GOLD_BOOTS).setName(ChatUtil.fixColor("{c}&lDystans")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Przebyty dystans&8: {c}"+StringUtil.formatDistance(stat.getDistanceTraveled()))));
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
        this.plugin.getQuestManager().getQuests().keySet().forEach(quest -> {
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
                itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: &c&lWykonane", "&8>> {n}Nagroda&8: {c}"+quest.getReward()+" {n}monet")));
            }else{
                itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+this.plugin.getQuestManager().getAmountByQuest(user, questType)+"&8/{c}"+quest.getAmount(), "&8>> {n}Nagroda&8: {c}"+quest.getReward()+" {n}monet")));
                if(questType.equals(QuestType.SPENT_TIME)){
                    itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+TimeUtil.getTimeToString(user.getUserStat().getOnlineTime())+" &8[&f"+ TimeUnit.MILLISECONDS.toHours(user.getUserStat().getOnlineTime())+"&8] " +"/{c} "+quest.getAmount()+"h", "&8>> {n}Nagroda&8: {c}"+quest.getReward()+" {n}monet")));
                }
                if(questType.equals(QuestType.COMEBACK)){
                    itemBuilder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Postep&8: {c}"+user.getUserStat().getComebackDaysInRow()+"&8/{c}"+quest.getAmount(), "&8>> {n}Nagroda&8: {c}"+quest.getReward()+" {n}monet")));
                }
            }
            gui.addItem(itemBuilder.toItemStack());
        });
        gui.setItem(26, this.backGlass, new QuestAction(this.plugin, QuestActionType.OPEN_MENU, null, user));
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI playerCustomEffects(User user) {
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lEFEKTY &8]", InventoryType.HOPPER, true);
        user.addInventory(gui);
        ItemBuilder haste = new ItemBuilder(Material.GOLD_PICKAXE).setName(ChatUtil.fixColor("{c}&lSzybkie kopanie II")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Cena&8: {c}1000 {n}monet", "&8>> {n}Czas&8: {c}120 {n}sek")));
        ItemBuilder jump = new ItemBuilder(Material.GOLD_BOOTS).setName(ChatUtil.fixColor("{c}&lWysokie skakanie")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Cena&8: {c}450 {n}monet", "&8>> {n}Czas&8: {c}120 {n}sek")));
        ItemBuilder damage = new ItemBuilder(Material.BLAZE_POWDER).setName(ChatUtil.fixColor("{c}&lOdpornosc na ogien I")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Cena&8: {c}800 {n}monet", "&8>> {n}Czas&8: {c}60 {n}sek")));
        ItemBuilder vision = new ItemBuilder(Material.EYE_OF_ENDER).setName(ChatUtil.fixColor("{c}&lWidzenie w ciemnosci")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Cena&8: {c}125 {n}monet", "&8>> {n}Czas&8: {c}260 {n}sek")));
        ItemBuilder speed = new ItemBuilder(Material.FEATHER).setName(ChatUtil.fixColor("{c}&lSzybkosc")).setLore(ChatUtil.fixColor(Arrays.asList(" ",
                "&8>> {n}Cena&8: {c}975 {n}monet", "&8>> {n}Czas&8: {c}60 {n}sek")));
        gui.setItem(0, haste.toItemStack(), new CustomEffectsAction(this.plugin, new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 120, 1), 1000, "Szybkie kopanie II", user));
        gui.setItem(1, jump.toItemStack(), new CustomEffectsAction(this.plugin, new PotionEffect(PotionEffectType.JUMP, 20 * 120, 0), 450, "Wysokie skakanie", user));
        gui.setItem(2, damage.toItemStack(), new CustomEffectsAction(this.plugin, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60, 0), 800, "Odpornosc na ogien I", user));
        gui.setItem(3, vision.toItemStack(), new CustomEffectsAction(this.plugin, new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 260, 0), 125, "Widzenie w ciemnosci", user));
        gui.setItem(4, speed.toItemStack(), new CustomEffectsAction(this.plugin, new PotionEffect(PotionEffectType.SPEED, 20 * 60, 0), 975, "Szybkosc", user));
        return gui;
    }

    public InventoryGUI playerBackups(User user, User backupUser){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lLISTA BACKUPOW &8]", 6, true);
        user.addInventory(gui);
        backupUser.getSortedBackups().forEach(backup -> {
            ItemBuilder backupItem = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{n}Gracz {c}"+backupUser.getName()))
                    .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Powod&8: {c}"+backup.getReason(), "&8>> {n}Czas&8: {c}"+TimeUtil.getDate(backup.getDeathTime()),
                            "&8>> {n}Ping&8: {c}"+backup.getPing())));
            gui.addItem(backupItem.toItemStack(), new PlayerBackupAction(this.plugin, PlayerBackupActionType.CHOOSE_BACKUP, backup, user));
        });
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI playerBackup(User user, Backup backup){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lBACKUP &8]", 3, true);
        ItemBuilder items = new ItemBuilder(Material.WOOD_PICKAXE).setName(ChatUtil.fixColor("{c}&lPrzedmioty"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Odda przedmioty&8: "+(backup.isGiveItems() ? "&aTak" : "&cNie"))));
        ItemBuilder armor = new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(ChatUtil.fixColor("{c}&lZbroja"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Odda zbroje&8: "+(backup.isGiveArmor() ? "&aTak" : "&cNie"))));
        ItemBuilder deaths = new ItemBuilder(Material.SKULL_ITEM).setName(ChatUtil.fixColor("{c}&lSmierci"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ustawi stara ilosc smierci&8: "+(backup.isGiveDeaths() ? "&aTak" : "&cNie"),
                        "&8>> {n}Smierci&8: {c}"+backup.getDeaths())));
        ItemBuilder points = new ItemBuilder(Material.WOOD_SWORD).setName(ChatUtil.fixColor("{c}&lPunkty"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Ustawi stara ilosc punktow&8: "+(backup.isGivePoints() ? "&aTak" : "&cNie"),
                        "&8>> {n}Punktow&8: {c}"+backup.getPoints())));
        gui.setItem(1, items.toItemStack(), new PlayerBackupAction(this.plugin, PlayerBackupActionType.CHANGE_ITEMS, backup, user));
        gui.setItem(3, armor.toItemStack(), new PlayerBackupAction(this.plugin, PlayerBackupActionType.CHANGE_ARMOR, backup, user));
        gui.setItem(5, deaths.toItemStack(), new PlayerBackupAction(this.plugin, PlayerBackupActionType.CHANGE_DEATHS, backup, user));
        gui.setItem(7, points.toItemStack(), new PlayerBackupAction(this.plugin, PlayerBackupActionType.CHANGE_POINTS, backup, user));
        gui.setItem(21, this.backGlass, new PlayerBackupAction(this.plugin, PlayerBackupActionType.OPEN_BACKUP_MENU, backup, user));
        gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short) 5).setName(ChatUtil.fixColor("&aPrzyznaj backup")).toItemStack(),
                new PlayerBackupAction(this.plugin, PlayerBackupActionType.ACCEPT_BACKUP, backup, user));
        gui.setItem(23, this.backGlass, new PlayerBackupAction(this.plugin, PlayerBackupActionType.OPEN_BACKUP_MENU, backup, user));
        gui.setEmptyItem(this.blank);
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI playerIncognito(User user){
        UserIncognito incognito = user.getIncognito();
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lMENU INCOGNITO &8]", 1, true);
        user.addInventory(gui);
        ItemBuilder nick = new ItemBuilder(Material.BOOK_AND_QUILL).setName(ChatUtil.fixColor("{c}&lUkrycie nicku"));
        if(incognito.getShowNickName().equals(IncognitoType.ALL)){
           nick.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj nick beda mogli zobaczyc&8:", "&8>> {n}wszyscy gracze", " ", "&8>> {n}Kliknij aby zmienic&8!")));
        }else{
            if(incognito.getShowNickName().equals(IncognitoType.GUILD)){
                if(user.hasGuild()){
                    nick.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj nick beda mogli zobaczyc&8:", "&8>> {n}czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    nick.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojego nicku", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }else{
                if(user.hasGuild()){
                    nick.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj nick beda mogli zobaczyc&8:", "&8>> {n}sojusznicy oraz czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    nick.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojego nicku", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }
        }
        ItemBuilder guildTag = new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{c}&lUkrycie tagu"));
        if(incognito.getShowGuildTag().equals(IncognitoType.ALL)){
            guildTag.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj tag beda mogli zobaczyc&8:", "&8>> {n}wszyscy gracze", " ", "&8>> {n}Kliknij aby zmienic&8!")));
        }else{
            if(incognito.getShowGuildTag().equals(IncognitoType.GUILD)){
                if(user.hasGuild()){
                    guildTag.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj tag beda mogli zobaczyc&8:", "&8>> {n}czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    guildTag.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojego tagu", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }else{
                if(user.hasGuild()){
                    guildTag.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj tag beda mogli zobaczyc&8:", "&8>> {n}sojusznicy oraz czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    guildTag.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojego tagu", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }
        }
        ItemBuilder skin = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(user.getName()).setName(ChatUtil.fixColor("{c}&lUkrycie skina"));
        skin.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoj oryginalny skin jest&8: ", "&8>> {n}"+(incognito.isHideOriginalSkin() ? "niewidoczny" : "widoczny")+" dla innych graczy", " ", "&8>> {n}Kliknij aby zmienic&8!")));
        ItemBuilder points = new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatUtil.fixColor("{c}&lUkrycie punktow"));
        if(incognito.getShowPoints().equals(IncognitoType.ALL)){
            points.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoje punkty beda mogli zobaczyc&8:", "&8>> {n}wszyscy gracze", " ", "&8>> {n}Kliknij aby zmienic&8!")));
        }else{
            if(incognito.getShowPoints().equals(IncognitoType.GUILD)){
                if(user.hasGuild()){
                    points.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoje punkty beda mogli zobaczyc&8:", "&8>> {n}czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    points.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twoich punktow", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }else{
                if(user.hasGuild()){
                    points.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoje punkty beda mogli zobaczyc&8:", "&8>> {n}sojusznicy oraz czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    points.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twoich punktow", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }
        }
        ItemBuilder rank = new ItemBuilder(Material.PAPER).setName(ChatUtil.fixColor("{c}&lUkrycie rangi"));
        if(incognito.getShowRank().equals(IncognitoType.ALL)){
            rank.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoja range beda mogli zobaczyc&8:", "&8>> {n}wszyscy gracze", " ", "&8>> {n}Kliknij aby zmienic&8!")));
        }else{
            if(incognito.getShowRank().equals(IncognitoType.GUILD)){
                if(user.hasGuild()){
                    rank.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoja range beda mogli zobaczyc&8:", "&8>> {n}czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    rank.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojej rangi", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }else{
                if(user.hasGuild()){
                    rank.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Twoja range beda mogli zobaczyc&8:", "&8>> {n}sojusznicy oraz czlonkowie twojej gildii", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }else{
                    rank.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Nikt nie bedzie mogl zobaczyc twojej rangi", " ", "&8>> {n}Kliknij aby zmienic&8!")));
                }
            }
        }
        gui.setItem(0, nick.toItemStack(), new IncognitoAction(this.plugin, IncognitoActionType.CHANGE_NICK, user));
        gui.setItem(2, guildTag.toItemStack(), new IncognitoAction(this.plugin, IncognitoActionType.CHANGE_TAG, user));
        gui.setItem(4, skin.toItemStack(), new IncognitoAction(this.plugin, IncognitoActionType.CHANGE_SKIN, user));
        gui.setItem(6, points.toItemStack(), new IncognitoAction(this.plugin, IncognitoActionType.CHANGE_POINTS, user));
        gui.setItem(8, rank.toItemStack(), new IncognitoAction(this.plugin, IncognitoActionType.CHANGE_RANK, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI blocksExchange(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lWYMIANA BLOKOW &8]", 1, true);
        user.addInventory(gui);
        ItemBuilder diamond = new ItemBuilder(Material.DIAMOND_BLOCK).setName(ChatUtil.fixColor("&8[ &3&lDiamenty &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby wymienic &bdiamenty", "&8>> &fna &3diamentowe &fbloki")));
        ItemBuilder emerald = new ItemBuilder(Material.EMERALD_BLOCK).setName(ChatUtil.fixColor("&8[ &2&lEmeraldy &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby wymienic &aemeraldy", "&8>> &fna &2emeraldowe &fbloki")));
        ItemBuilder redstone = new ItemBuilder(Material.REDSTONE_BLOCK).setName(ChatUtil.fixColor("&8[ &c&lRedstone &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby wymienic &credstone", "&8>> &fna &4redstone'owe &fbloki")));
        ItemBuilder iron = new ItemBuilder(Material.IRON_BLOCK).setName(ChatUtil.fixColor("&8[ &f&lZelazo &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby wymienic sztabki &7zelaza", "&8>> &fna &f&lzelazne &fbloki")));
        ItemBuilder gold = new ItemBuilder(Material.GOLD_BLOCK).setName(ChatUtil.fixColor("&8[ &6&lZloto &8]"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby wymienic sztabki &ezlota", "&8>> &fna &6zlote &fbloki")));
        gui.setItem(0, diamond.toItemStack(), new BlocksExchangeAction(this.plugin, Material.DIAMOND, user));
        gui.setItem(2, emerald.toItemStack(), new BlocksExchangeAction(this.plugin, Material.EMERALD, user));
        gui.setItem(4, gold.toItemStack(), new BlocksExchangeAction(this.plugin, Material.GOLD_INGOT, user));
        gui.setItem(6, iron.toItemStack(), new BlocksExchangeAction(this.plugin, Material.IRON_INGOT, user));
        gui.setItem(8, redstone.toItemStack(), new BlocksExchangeAction(this.plugin, Material.REDSTONE, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI magicCaseDraw(User user, MagicCaseType magicCaseType){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lLOSOWANIE... &8]", 3, true);
        user.addInventory(gui);
        for(int i = 9; i < 18; i++){
            MagicCaseDrop magicCaseDrop = this.plugin.getMagicCaseManager().getCaseDrop(magicCaseType).get(RandomUtil.getRandInt(0, this.plugin.getMagicCaseManager().getCaseDrop(magicCaseType).size() - 1));
            ItemStack itemStack = new ItemBuilder(magicCaseDrop.getItemStack()).setAmount(RandomUtil.getRandInt(magicCaseDrop.getMinAmount(), magicCaseDrop.getMaxAmount())).toItemStack();
            gui.setItem(i, itemStack);
        }
        gui.setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("&6&lWygrana")).toItemStack());
        gui.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)5).setName(ChatUtil.fixColor("&6&lWygrana")).toItemStack());
        gui.setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)13).toItemStack());
        gui.setItem(5, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)13).toItemStack());
        gui.setItem(21, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)13).toItemStack());
        gui.setItem(23, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)13).toItemStack());
        gui.setEmptyItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)7).toItemStack());
        return gui;
    }

    public InventoryGUI magicCaseMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lMAGICZNA SKRZYNIA &8]", 3, true);
        user.addInventory(gui);
        ItemStack redPane = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)14).setName(" ").toItemStack();
        gui.setItem(1, redPane);
        gui.setItem(3, redPane);
        gui.setItem(5, redPane);
        gui.setItem(7, redPane);
        gui.setItem(11, redPane);
        gui.setItem(15, redPane);
        gui.setItem(19, redPane);
        gui.setItem(21, redPane);
        gui.setItem(23, redPane);
        gui.setItem(25, redPane);
        gui.setItem(2, new ItemBuilder(Material.TRIPWIRE_HOOK).setName(ChatUtil.fixColor("&b&lFragmenty standardowego klucza"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fPosiadasz&8: &b"+user.getUserStat().getKeyFragmentsByMagicCaseType(MagicCaseType.NORMAL)
                , "&8>> &fWymagane do utworzenia klucza&8: &b"+this.plugin.getMagicCaseManager().getFragmentsRequiredByMagicCaseType(MagicCaseType.NORMAL),
                        " ", "&8>> &fKliknij aby utworzyc klucz!"))).toItemStack(), new MagicCaseAction(this.plugin, MagicCaseActionType.CHANGE_FRAGMENTS, MagicCaseType.NORMAL, user));
        gui.setItem(6, new ItemBuilder(Material.TRIPWIRE_HOOK).setName(ChatUtil.fixColor("&5&lFragmenty standardowego klucza"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fPosiadasz&8: &5"+user.getUserStat().getKeyFragmentsByMagicCaseType(MagicCaseType.PREMIUM)
                        , "&8>> &fWymagane do utworzenia klucza&8: &5"+this.plugin.getMagicCaseManager().getFragmentsRequiredByMagicCaseType(MagicCaseType.PREMIUM),
                        " ", "&8>> &fKliknij aby utworzyc klucz!"))).toItemStack(), new MagicCaseAction(this.plugin, MagicCaseActionType.CHANGE_FRAGMENTS, MagicCaseType.PREMIUM, user));
        gui.setItem(20, new ItemBuilder(Material.CHEST).setName(ChatUtil.fixColor("&b&lDrop ze standardowej skrzyni")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby zobaczyc!"))).toItemStack(), new MagicCaseAction(this.plugin, MagicCaseActionType.OPEN_DROP, MagicCaseType.NORMAL, user));
        gui.setItem(24, new ItemBuilder(Material.ENDER_CHEST).setName(ChatUtil.fixColor("&5&lDrop z wyjatkowej skrzyni")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby zobaczyc!"))).toItemStack(), new MagicCaseAction(this.plugin, MagicCaseActionType.OPEN_DROP, MagicCaseType.PREMIUM, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI magicCaseDrop(User user, MagicCaseType magicCaseType){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDROP ZE SKRZYNI &8]", 5, true);
        for(MagicCaseDrop magicCaseDrop : this.plugin.getMagicCaseManager().getCaseDrop(magicCaseType)){
            ItemBuilder dropItem = new ItemBuilder(magicCaseDrop.getItemStack().clone());
            if(magicCaseDrop.getName() != null){
                dropItem.setName(ChatUtil.fixColor(magicCaseDrop.getName()));
            }
            dropItem.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fWypada w ilosci&8: {c}"+(magicCaseDrop.getMinAmount() == magicCaseDrop.getMaxAmount() ? magicCaseDrop.getMaxAmount() : magicCaseDrop.getMinAmount()+"&8-{c}"+magicCaseDrop.getMaxAmount()))));
            gui.addItem(dropItem.toItemStack());
        }
        gui.setItem(44, this.backGlass, new MagicCaseAction(this.plugin, MagicCaseActionType.BACK_TO_MENU, null, user));
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI customEnchantMenu(User user, ItemStack itemStack, CustomEnchantItemEnum customEnchantItemEnum){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lENCHANT &8]", 5, true);
        int slot = 10;
        int counter = 0;
        for(Enchantment enchantment : this.plugin.getCustomEnchantManager().enchantmentsByItemType(customEnchantItemEnum)){
            if(counter >= 3){
                slot += 9-3;
                counter = 0;
            }
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, enchantment.getStartLevel(), true);
            enchantedBook.setItemMeta(enchantmentStorageMeta);
            gui.setItem(slot, enchantedBook, new CustomEnchantEnchantmentChoiceAction(this.plugin, user, itemStack, enchantment, customEnchantItemEnum));
            slot++;
            counter++;
        }
        ItemStack stainedGlassPane = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ")
                .setDurability((short) 14).toItemStack();
        gui.setItem(15, stainedGlassPane);
        gui.setItem(33, stainedGlassPane);
        gui.setItem(23, stainedGlassPane);
        gui.setItem(25, stainedGlassPane);
        gui.setItem(24, new ItemStack(itemStack.getType()));
        gui.setEmptyItem(this.blank.clone());
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI customEnchantLevelChoice(User user, ItemStack itemStack, Enchantment enchantment, CustomEnchantItemEnum customEnchantItemEnum){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lWybor poziomu &8]", 1, true);
        Integer maxLevelRestrict = ConfigManager.getEnchantmentIntegerMap().get(enchantment);
        for(int i = enchantment.getStartLevel(); i <= enchantment.getMaxLevel(); i++){
            if(Objects.nonNull(maxLevelRestrict) && i > maxLevelRestrict){
                break;
            }
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, i, true);
            enchantedBook.setItemMeta(enchantmentStorageMeta);
            ItemMeta itemMeta = enchantedBook.getItemMeta();
            itemMeta.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKoszt&8: {c}"+ (i*4) + " lvl &8(&7XP&8)")));
            enchantedBook.setItemMeta(itemMeta);
            gui.addItem(enchantedBook, new CustomEnchantLevelChoiceAction(this.plugin, user, itemStack, enchantment, i, (i*4), customEnchantItemEnum, false));
        }
        gui.setItem(8, this.backGlass, new CustomEnchantLevelChoiceAction(this.plugin, user, itemStack, enchantment, 0, 0, customEnchantItemEnum, true));
        user.addInventory(gui);
        return gui;
    }

    public InventoryGUI userRewards(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lNAGRODY &8]", 1, true);
        user.addInventory(gui);
        ItemBuilder discordReward = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3)
                .setCustomSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0=")
                .setName(ChatUtil.fixColor("&5&lNagroda DISCORD")).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby odebrac")));
        ItemBuilder clientReward = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3)
                .setCustomSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYwMDIzZTNkOTUwZGE5ZGFjMDlkYWNjNWNkNGIxMjA0OWYwNTJjMjU5YWRiYzlhYzQ3ZjFjNTIyZmNlYmY0MiJ9fX0=")
                .setName(ChatUtil.fixColor("&6&lNagroda NsClient"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> &fKliknij aby odebrac")));
        gui.setItem(1, discordReward.toItemStack(), new RewardAction(this.plugin, RewardActionType.DISCORD, user));
        gui.setItem(7, clientReward.toItemStack(), new RewardAction(this.plugin, RewardActionType.CLIENT, user));
        gui.setEmptyItem(this.blank);
        return gui;
    }
}
