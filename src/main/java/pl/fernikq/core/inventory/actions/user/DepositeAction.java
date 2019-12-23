package pl.fernikq.core.inventory.actions.user;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.DepositeActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;

public class DepositeAction implements InventoryAction {

    private final CorePlugin plugin;
    private final User user;
    private final DepositeActionType depositeActionType;

    public DepositeAction(CorePlugin plugin, DepositeActionType depositeActionType, User user){
        this.plugin = plugin;
        this.user = user;
        this.depositeActionType = depositeActionType;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(depositeActionType.equals(DepositeActionType.TAKE_APPLES)){
            int applesInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.GOLDEN_APPLE, (short) 0);
            if(user.getUserStat().getDepositeApples() <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz refili w schowku!"));
                return;
            }
            int toGive = ConfigManager.maxGoldenApplesInInventory - applesInInventory;
            if(toGive <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz maksymalna ilosc refili w ekwipunku!"));
                return;
            }
            if(toGive > user.getUserStat().getDepositeApples()){
                toGive = user.getUserStat().getDepositeApples();
            }
            ItemUtil.giveItems(player, new ItemStack(Material.GOLDEN_APPLE, toGive));
            user.getUserStat().removeDepositeApples(toGive);
            ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+toGive+" {n}"+correctName(toGive, DepositeActionType.TAKE_APPLES)+" ze schowka&8!");
            this.plugin.getUserInventory().deposite(user).openInventory(player);
            return;
        }
        if(depositeActionType.equals(DepositeActionType.TAKE_ENCHANTED_APPLES)) {
            int enchantedApplesInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.GOLDEN_APPLE, (short) 1);
            if(user.getUserStat().getDepositeEnchantedApples() <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz koxow w schowku!"));
                return;
            }
            int toGive = ConfigManager.maxEnchantedGoldenApplesInInventory - enchantedApplesInInventory;
            if(toGive <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz maksymalna ilosc koxow w ekwipunku!"));
                return;
            }
            if(toGive > user.getUserStat().getDepositeEnchantedApples()){
                toGive = user.getUserStat().getDepositeEnchantedApples();
            }
            ItemUtil.giveItems(player, new ItemStack(Material.GOLDEN_APPLE, toGive, (short) 1));
            user.getUserStat().removeDepositeEnchantedApples(toGive);
            ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+toGive+" {n}"+correctName(toGive, DepositeActionType.TAKE_ENCHANTED_APPLES)+" ze schowka&8!");
            this.plugin.getUserInventory().deposite(user).openInventory(player);
            return;
        }
        if(depositeActionType.equals(DepositeActionType.TAKE_PEARLS)) {
            int pearlsInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.ENDER_PEARL, (short) 0);
            if(user.getUserStat().getDepositePearls() <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie posiadasz perel w schowku!"));
                return;
            }
            int toGive = ConfigManager.maxPearlsInInventory - pearlsInInventory;
            if(toGive <= 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Posiadasz maksymalna ilosc perel w ekwipunku!"));
                return;
            }
            if(toGive > user.getUserStat().getDepositePearls()){
                toGive = user.getUserStat().getDepositePearls();
            }
            ItemUtil.giveItems(player, new ItemStack(Material.ENDER_PEARL, toGive));
            user.getUserStat().removeDepositePearls(toGive);
            ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+toGive+" {n}"+correctName(toGive, DepositeActionType.TAKE_PEARLS)+" ze schowka&8!");
            this.plugin.getUserInventory().deposite(user).openInventory(player);
            return;
        }
        if(depositeActionType.equals(DepositeActionType.TAKE_ALL)) {
            int applesInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.GOLDEN_APPLE, (short) 0);
            int pearlsInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.ENDER_PEARL, (short) 0);
            int enchantedApplesInInventory = ItemUtil.getAmountOfMaterial(player.getInventory(), Material.GOLDEN_APPLE, (short) 1);
            int applesToGive = 0;
            int pearlsToGive = 0;
            int enchantedApplesToGive = 0;

            if(user.getUserStat().getDepositeEnchantedApples() > 0){
                enchantedApplesToGive = ConfigManager.maxEnchantedGoldenApplesInInventory - enchantedApplesInInventory;
                if(enchantedApplesToGive > 0){
                    if(enchantedApplesToGive > user.getUserStat().getDepositeEnchantedApples()){
                        enchantedApplesToGive = user.getUserStat().getDepositeEnchantedApples();
                    }
                }
            }

            if(user.getUserStat().getDepositeApples() > 0){
                applesToGive = ConfigManager.maxGoldenApplesInInventory - applesInInventory;
                if(applesToGive > 0){
                    if(applesToGive > user.getUserStat().getDepositeApples()){
                        applesToGive = user.getUserStat().getDepositeApples();
                    }
                }
            }

            if(user.getUserStat().getDepositePearls() > 0) {
                pearlsToGive = ConfigManager.maxPearlsInInventory - pearlsInInventory;
                if(pearlsToGive > 0) {
                    if(pearlsToGive > user.getUserStat().getDepositePearls()) {
                        pearlsToGive = user.getUserStat().getDepositePearls();
                    }
                }
            }

            if(enchantedApplesToGive == 0 && applesToGive == 0 && pearlsToGive == 0){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz niczego wyplacic!"));
                return;
            }

            if(applesToGive > 0){
                ItemUtil.giveItems(player, new ItemStack(Material.GOLDEN_APPLE, applesToGive));
                user.getUserStat().removeDepositeApples(applesToGive);
                ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+applesToGive+" {n}"+correctName(applesToGive, DepositeActionType.TAKE_APPLES)+" ze schowka&8!");
            }
            if(enchantedApplesToGive > 0){
                ItemUtil.giveItems(player, new ItemStack(Material.GOLDEN_APPLE, enchantedApplesToGive, (short) 1));
                user.getUserStat().removeDepositeEnchantedApples(enchantedApplesToGive);
                ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+enchantedApplesToGive+" {n}"+correctName(enchantedApplesToGive, DepositeActionType.TAKE_ENCHANTED_APPLES)+" ze schowka&8!");
            }
            if(pearlsToGive > 0){
                ItemUtil.giveItems(player, new ItemStack(Material.ENDER_PEARL, pearlsToGive));
                user.getUserStat().removeDepositePearls(pearlsToGive);
                ChatUtil.sendMessage(player, "&8>> {n}Wyplaciles {c}"+pearlsToGive+" {n}"+correctName(pearlsToGive, DepositeActionType.TAKE_PEARLS)+" ze schowka&8!");
            }
            this.plugin.getUserInventory().deposite(user).openInventory(player);
            return;
        }
    }

    public String correctName(int amount, DepositeActionType type){
        if(type.equals(DepositeActionType.TAKE_PEARLS)) {
            if(amount == 1){
                return "perle";
            }
            if(amount < 5){
                return "perly";
            }
            return "perel";
        }
        if(type.equals(DepositeActionType.TAKE_APPLES)) {
            if(amount == 1){
                return "refila";
            }
            if(amount < 5){
                return "refile";
            }
            return "refili";
        }
        if(type.equals(DepositeActionType.TAKE_ENCHANTED_APPLES)) {
            if(amount == 1){
                return "koxa";
            }
            if(amount < 5){
                return "koxy";
            }
            return "koxow";
        }
        return "";
    }
}
