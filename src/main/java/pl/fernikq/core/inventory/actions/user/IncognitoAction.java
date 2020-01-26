package pl.fernikq.core.inventory.actions.user;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.user.ChatSettingsActionType;
import pl.fernikq.core.inventory.enums.user.IncognitoActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.incognito.IncognitoType;
import pl.fernikq.core.user.incognito.UserIncognito;
import pl.fernikq.core.util.ChatUtil;

import java.util.concurrent.TimeUnit;

public class IncognitoAction implements InventoryAction {

    private User user;
    private CorePlugin plugin;
    private IncognitoActionType type;

    public IncognitoAction(CorePlugin plugin, IncognitoActionType type, User user){
        this.user = user;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        UserIncognito incognito = user.getIncognito();
        if(type.equals(IncognitoActionType.CHANGE_SKIN)){
            if(!incognito.hasSkin()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz zmienic skina, poniewaz go nie posiadasz!"));
                return;
            }
            if(user.getIncognito().getSkinChangeTime() > System.currentTimeMillis()){
                ChatUtil.sendMessage(player, MessagesManager.error("Odczekaj chwile przed kolejna zmiana!"));
                return;
            }
            user.getIncognito().setSkinChangeTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20));
            this.plugin.getIncognitoManager().changeSkin(user, incognito.isHideOriginalSkin());
            ChatUtil.sendMessage(player, "&8>> {n}Twoj skin zostal zmieniony!");
            this.plugin.getUserInventory().playerIncognito(user).openInventory(player);
            return;
        }
        if(type.equals(IncognitoActionType.CHANGE_TAG)){
            if(!user.hasGuild()){
                ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz tego uzyc poniewaz nie posiadasz gidlii!"));
                return;
            }else{
                if(incognito.getShowGuildTag().equals(IncognitoType.ALL)) {
                    incognito.setShowGuildTag(IncognitoType.ALLIES);
                }else{
                    if(incognito.getShowGuildTag().equals(IncognitoType.ALLIES)){
                        incognito.setShowGuildTag(IncognitoType.GUILD);
                    }else {
                        incognito.setShowGuildTag(IncognitoType.ALL);
                    }
                }
            }
            this.plugin.getTagManager().updateTag(player);
            this.plugin.getUserInventory().playerIncognito(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Tryb incognito zostal zmieniony!");
            return;
        }
        if(type.equals(IncognitoActionType.CHANGE_NICK)){
            if(!user.hasGuild()){
                if(incognito.getShowNickName().equals(IncognitoType.ALL)){
                    incognito.setShowNickName(IncognitoType.GUILD);
                }else{
                    incognito.setShowNickName(IncognitoType.ALL);
                }
            }else{
                if(incognito.getShowNickName().equals(IncognitoType.ALL)) {
                    incognito.setShowNickName(IncognitoType.ALLIES);
                }else{
                    if(incognito.getShowNickName().equals(IncognitoType.ALLIES)){
                        incognito.setShowNickName(IncognitoType.GUILD);
                    }else {
                        incognito.setShowNickName(IncognitoType.ALL);
                    }
                }
            }
            this.plugin.getTagManager().updateTag(player);
            this.plugin.getUserInventory().playerIncognito(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Tryb incognito zostal zmieniony!");
            return;
        }
        if(type.equals(IncognitoActionType.CHANGE_POINTS)){
            if(!user.hasGuild()){
                if(incognito.getShowPoints().equals(IncognitoType.ALL)){
                    incognito.setShowPoints(IncognitoType.GUILD);
                }else{
                    incognito.setShowPoints(IncognitoType.ALL);
                }
            }else{
                if(incognito.getShowPoints().equals(IncognitoType.ALL)) {
                    incognito.setShowPoints(IncognitoType.ALLIES);
                }else{
                    if(incognito.getShowPoints().equals(IncognitoType.ALLIES)){
                        incognito.setShowPoints(IncognitoType.GUILD);
                    }else {
                        incognito.setShowPoints(IncognitoType.ALL);
                    }
                }
            }
            this.plugin.getDummyManager().updateScore(user);
            this.plugin.getUserInventory().playerIncognito(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Tryb incognito zostal zmieniony!");
            return;
        }
        if(type.equals(IncognitoActionType.CHANGE_RANK)){
            if(!user.hasGuild()){
                if(incognito.getShowRank().equals(IncognitoType.ALL)){
                    incognito.setShowRank(IncognitoType.GUILD);
                }else{
                    incognito.setShowRank(IncognitoType.ALL);
                }
            }else{
                if(incognito.getShowRank().equals(IncognitoType.ALL)) {
                    incognito.setShowRank(IncognitoType.ALLIES);
                }else{
                    if(incognito.getShowRank().equals(IncognitoType.ALLIES)){
                        incognito.setShowRank(IncognitoType.GUILD);
                    }else {
                        incognito.setShowRank(IncognitoType.ALL);
                    }
                }
            }
            this.plugin.getTagManager().updateTag(player);
            this.plugin.getUserInventory().playerIncognito(user).openInventory(player);
            ChatUtil.sendMessage(player, "&8>> {n}Tryb incognito zostal zmieniony!");
            return;
        }
    }
}
