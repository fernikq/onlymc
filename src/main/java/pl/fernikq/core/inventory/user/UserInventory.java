package pl.fernikq.core.inventory.user;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.inventory.actions.CraftingAction;
import pl.fernikq.core.inventory.actions.KitAction;
import pl.fernikq.core.inventory.actions.ShopAction;
import pl.fernikq.core.inventory.enums.CraftingActionType;
import pl.fernikq.core.inventory.enums.KitActionType;
import pl.fernikq.core.inventory.enums.ShopActionType;
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
        blank = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).setName("").toItemStack();
        color = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3)).setName("").toItemStack();
        backGlass = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
        backBarrier = new ItemBuilder(new ItemStack(Material.BARRIER, 1)).setName(ChatUtil.fixColor("&c&lPowrot")).toItemStack();
    }

    public InventoryGUI kitMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDostepne zestawy &8]", 1, true);
        user.addInventory(gui);
        for(Kit kit : this.plugin.getKitManager().getKits()){
            ItemBuilder builder = new ItemBuilder(kit.getItem().clone()).setAmount(1).setName(ChatUtil.fixColor(kit.getName()));
            builder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Zestaw dostepny dla "+(kit.getGroup().equals(UserGroup.PLAYER) ? "&7Gracza" : kit.getGroup().getPrefix()), "&8>> {n}Kliknij aby obejrzec")));
            gui.addItem(builder.toItemStack(), new KitAction(this.plugin, kit, KitActionType.CHOOSE));
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
        gui.setItem(52, back.toItemStack(), new KitAction(this.plugin, KitActionType.BACK));
        gui.setItem(53, take.toItemStack(), new KitAction(this.plugin, kit, KitActionType.TAKE));
        return gui;
    }

    public InventoryGUI craftingsMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lDostepne craftingi &8]", 1, true);
        user.addInventory(gui);
        for(Generator generator : this.plugin.getGeneratorManager().getGenerators()){
            ItemBuilder builder = new ItemBuilder(generator.getItemStack().clone()).setAmount(1);
            builder.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby zobaczyc crafting")));
            gui.addItem(builder.toItemStack(), new CraftingAction(plugin, CraftingActionType.CHOOSE, generator));
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
        gui.setItem(43, backBarrier, new CraftingAction(plugin, CraftingActionType.BACK));
        ItemBuilder autoCraft = new ItemBuilder(Material.WORKBENCH).setName(ChatUtil.fixColor("&a&lAutomatyczny crafting"));
        if(generator.hasItems(user.asPlayer())){
            autoCraft.setLore(ChatUtil.fixColor(Arrays.asList(" ", "&aPosiadasz przedmioty!")));
        }else {
            autoCraft.addLore(" ");
            for(Map.Entry<Material, Integer> item : generator.getAmounts().entrySet()) {
                autoCraft.addLore(ChatUtil.fixColor("&8>> {n}" + item.getKey().name().toLowerCase() + "&8: {c}" + ItemUtil.getAmountOfMaterial(user.asPlayer().getInventory(), item.getKey(), (short) 0) + "&8/{c}" + item.getValue()));
            }
        }
        gui.setItem(44, autoCraft.toItemStack(), new CraftingAction(plugin, CraftingActionType.CRAFT, generator));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI shopMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSKLEP &8]", 1, true);
        user.addInventory(gui);
        gui.setItem(2, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 5).setName(ChatUtil.fixColor("&a&lKupno"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby kupic przedmioty&8!"))).toItemStack(), new ShopAction(this.plugin, ShopActionType.CHOOSE_BUY));
        gui.setItem(6, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 14).setName(ChatUtil.fixColor("&c&lSprzedaz"))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Kliknij aby sprzedac przedmioty&8!"))).toItemStack(), new ShopAction(this.plugin, ShopActionType.CHOOSE_SELL));
        gui.setEmptyItem(this.blank);
        return gui;
    }

    public InventoryGUI shopBuyMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lKUPNO - RODZAJ &8]", 2, true);
        user.addInventory(gui);
        for(Shop shop : this.plugin.getShopManager().getShops(ShopType.BUY)){
            gui.addItem(shop.getItem().clone(), new ShopAction(this.plugin, shop, ShopActionType.CHOOSE_BUY_TYPE));
        }
        gui.setItem(17, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_MENU));
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
            gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, ShopActionType.BUY_ITEM));
        }
        gui.setItem(53, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_BUY_MENU));
        return gui;
    }

    public InventoryGUI shopSellMenu(User user){
        InventoryGUI gui = new InventoryGUI("&8[ {c}&lSPRZEDAZ - RODZAJ &8]", 2, true);
        user.addInventory(gui);
        for(Shop shop : this.plugin.getShopManager().getShops(ShopType.SELL)){
            gui.addItem(shop.getItem().clone(), new ShopAction(this.plugin, shop, ShopActionType.CHOOSE_SELL_TYPE));
        }
        gui.setItem(17, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_MENU));
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
            gui.addItem(itemBuilder.toItemStack(), new ShopAction(this.plugin, shopItem, ShopActionType.SELL_ITEM));
        }
        gui.setItem(53, this.backGlass, new ShopAction(this.plugin, ShopActionType.BACK_SELL_MENU));
        return gui;
    }
}
