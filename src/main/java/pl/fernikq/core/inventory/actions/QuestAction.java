package pl.fernikq.core.inventory.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryAction;
import pl.fernikq.core.inventory.enums.QuestActionType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.quests.Quest;
import pl.fernikq.core.user.quests.QuestType;

public class QuestAction implements InventoryAction {

    private final CorePlugin plugin;
    private QuestActionType questActionType;
    private User user;
    private QuestType questType;

    public QuestAction(CorePlugin plugin, QuestActionType questActionType, QuestType questType, User user) {
        this.plugin = plugin;
        this.questActionType = questActionType;
        this.user = user;
        this.questType = questType;
    }

    @Override
    public void execute(Player player, Inventory inventory, int slot, ItemStack itemStack, InventoryClickEvent event) {
        if(questActionType.equals(QuestActionType.OPEN_MENU)){
            this.plugin.getUserInventory().playerQuestsMenu(user).openInventory(player);
            return;
        }
        if(questActionType.equals(QuestActionType.OPEN_QUEST_GUI)){
            if(questType.equals(QuestType.SPENT_TIME)){
                this.plugin.getQuestManager().checkQuest(user, QuestType.SPENT_TIME);
            }
            this.plugin.getUserInventory().playerQuest(user, questType).openInventory(player);
            return;
        }
    }
}
