package pl.fernikq.core.user.quests;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class QuestManager {

    private final CorePlugin plugin;
    private List<Quest> quests;
    private List<QuestType> questTypes;

    public QuestManager(CorePlugin plugin){
        this.plugin = plugin;
        this.quests = new ArrayList<>();

        this.quests.add(new Quest("Spedz na serwerze 2 godziny", QuestType.SPENT_TIME, 2, 500, 1));
        this.quests.add(new Quest("Spedz na serwerze 6 godzin", QuestType.SPENT_TIME, 6, 1500, 2));
        this.quests.add(new Quest("Spedz na serwerze 12 godzin", QuestType.SPENT_TIME, 12, 3000, 3));
        this.quests.add(new Quest("Spedz na serwerze 24 godziny", QuestType.SPENT_TIME, 24, 4000, 4));
        this.quests.add(new Quest("Spedz na serwerze 36 godzin", QuestType.SPENT_TIME, 36, 5500, 5));

        this.quests.add(new Quest("Zrab 64 sztuki drewna", QuestType.MINED_WOOD, 64, 500, 0));
        this.quests.add(new Quest("Zrab 128 sztuk drewna", QuestType.MINED_WOOD, 128, 1000, 0));
        this.quests.add(new Quest("Zrab 256 sztuk drewna", QuestType.MINED_WOOD, 256, 1500, 0));
        this.quests.add(new Quest("Zrab 512 sztuk drewna", QuestType.MINED_WOOD, 512, 2000, 0));
        this.quests.add(new Quest("Zrab 1024 sztuki drewna", QuestType.MINED_WOOD, 1024, 2500, 0));

        this.quests.add(new Quest("Wykop 500 sztuk kamienia", QuestType.MINED_STONE, 500, 250, 0));
        this.quests.add(new Quest("Wykop 1500 sztuk kamienia", QuestType.MINED_STONE, 1500, 750, 0));
        this.quests.add(new Quest("Wykop 5000 sztuk kamienia", QuestType.MINED_STONE, 5000, 1500, 0));
        this.quests.add(new Quest("Wykop 10000 sztuk kamienia", QuestType.MINED_STONE, 10000, 2500, 0));
        this.quests.add(new Quest("Wykop 50000 sztuk kamienia", QuestType.MINED_STONE, 50000, 5000, 0));

        this.quests.add(new Quest("Wejdz na serwer 3 dni z rzedu", QuestType.COMEBACK, 3, 1500, 1));
        this.quests.add(new Quest("Wejdz na serwer 5 dni z rzedu", QuestType.COMEBACK, 5, 1750, 2));
        this.quests.add(new Quest("Wejdz na serwer 7 dni z rzedu", QuestType.COMEBACK, 7, 2250, 3));
        this.quests.add(new Quest("Wejdz na serwer 12 dni z rzedu", QuestType.COMEBACK, 12, 2950, 4));
        this.quests.add(new Quest("Wejdz na serwer 14 dni z rzedu", QuestType.COMEBACK, 14, 3500, 5));

        this.quests.add(new Quest("Zlow 16 ryb", QuestType.CATCHED_FISH, 16, 500, 0));
        this.quests.add(new Quest("Zlow 32 ryb", QuestType.CATCHED_FISH, 32, 1000, 0));
        this.quests.add(new Quest("Zlow 64 ryb", QuestType.CATCHED_FISH, 64, 1500, 0));
        this.quests.add(new Quest("Zlow 128 ryb", QuestType.CATCHED_FISH, 128, 2000, 0));
        this.quests.add(new Quest("Zlow 256 ryb", QuestType.CATCHED_FISH, 256, 3000, 0));

        this.quests.add(new Quest("Wejdz na teren 3 roznych gildii", QuestType.EXPLORE_GUILDS, 3, 750, 0));
        this.quests.add(new Quest("Wejdz na teren 6 roznych gildii", QuestType.EXPLORE_GUILDS, 6, 1500, 0));
        this.quests.add(new Quest("Wejdz na teren 9 roznych gildii", QuestType.EXPLORE_GUILDS, 9, 2000, 0));
        this.quests.add(new Quest("Wejdz na teren 12 roznych gildii", QuestType.EXPLORE_GUILDS, 12, 2500, 0));
        this.quests.add(new Quest("Wejdz na teren 15 roznych gildii", QuestType.EXPLORE_GUILDS, 15, 3000, 0));

        this.quests.add(new Quest("Zabij 3 roznych graczy", QuestType.KILL_UNIQUE_USERS, 3, 500, 0));
        this.quests.add(new Quest("Zabij 6 roznych graczy", QuestType.KILL_UNIQUE_USERS, 6, 1000, 0));
        this.quests.add(new Quest("Zabij 9 roznych graczy", QuestType.KILL_UNIQUE_USERS, 9, 1500, 0));
        this.quests.add(new Quest("Zabij 12 roznych graczy", QuestType.KILL_UNIQUE_USERS, 12, 2000, 0));
        this.quests.add(new Quest("Zabij 15 roznych graczy", QuestType.KILL_UNIQUE_USERS, 15, 2500, 0));

        this.quests.add(new Quest("Zabij 3 graczy z ranga VIP+", QuestType.KILL_USERS_WITH_RANK, 3, 750, 0));
        this.quests.add(new Quest("Zabij 6 graczy z ranga VIP+", QuestType.KILL_USERS_WITH_RANK, 6, 1500, 0));
        this.quests.add(new Quest("Zabij 9 graczy z ranga VIP+", QuestType.KILL_USERS_WITH_RANK, 9, 2000, 0));
        this.quests.add(new Quest("Zabij 12 graczy z ranga VIP+", QuestType.KILL_USERS_WITH_RANK, 12, 2500, 0));
        this.quests.add(new Quest("Zabij 15 graczy z ranga VIP+", QuestType.KILL_USERS_WITH_RANK, 15, 3000, 0));

        this.quests.add(new Quest("Przejdz 2000 kratek", QuestType.TRAVELED_DISTANCE, 2000, 500, 0));
        this.quests.add(new Quest("Przejdz 5000 kratek", QuestType.TRAVELED_DISTANCE, 5000, 1000, 0));
        this.quests.add(new Quest("Przejdz 10000 kratek", QuestType.TRAVELED_DISTANCE, 10000, 1500, 0));
        this.quests.add(new Quest("Przejdz 15000 kratek", QuestType.TRAVELED_DISTANCE, 15000, 2500, 0));
        this.quests.add(new Quest("Przejdz 20000 kratek", QuestType.TRAVELED_DISTANCE, 20000, 4000, 0));

        this.quests.add(new Quest("Otworz 5 "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow", QuestType.OPENED_PREMIUMCASE, 5, 250, 0));
        this.quests.add(new Quest("Otworz 10 "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow", QuestType.OPENED_PREMIUMCASE, 10, 750, 0));
        this.quests.add(new Quest("Otworz 25 "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow", QuestType.OPENED_PREMIUMCASE, 25, 1000, 0));
        this.quests.add(new Quest("Otworz 50 "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow", QuestType.OPENED_PREMIUMCASE, 50, 1750, 0));
        this.quests.add(new Quest("Otworz 100 "+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow", QuestType.OPENED_PREMIUMCASE, 100, 2500, 0));

        this.quests.add(new Quest("Otworz 5 "+this.plugin.getDropManager().getCobblexNameInGUI()+"'ow", QuestType.OPENED_COBBLEX, 5, 250, 0));
        this.quests.add(new Quest("Otworz 10 "+this.plugin.getDropManager().getCobblexNameInGUI()+"'ow", QuestType.OPENED_COBBLEX, 10, 750, 0));
        this.quests.add(new Quest("Otworz 25 "+this.plugin.getDropManager().getCobblexNameInGUI()+"'ow", QuestType.OPENED_COBBLEX, 25, 1100, 0));
        this.quests.add(new Quest("Otworz 50 "+this.plugin.getDropManager().getCobblexNameInGUI()+"'ow", QuestType.OPENED_COBBLEX, 50, 1550, 0));
        this.quests.add(new Quest("Otworz 100 "+this.plugin.getDropManager().getCobblexNameInGUI()+"'ow", QuestType.OPENED_COBBLEX, 100, 2200, 0));

        this.quests.add(new Quest("Zdabadz 3 asysty", QuestType.ASSISTS, 3, 500, 0));
        this.quests.add(new Quest("Zdabadz 6 asyst", QuestType.ASSISTS, 6, 1000, 0));
        this.quests.add(new Quest("Zdabadz 9 asyst", QuestType.ASSISTS, 9, 1500, 0));
        this.quests.add(new Quest("Zdabadz 12 asyst", QuestType.ASSISTS, 12, 2000, 0));
        this.quests.add(new Quest("Zdabadz 15 asyst", QuestType.ASSISTS, 15, 2500, 0));

        this.quests.add(new Quest("Zdabadz 5 zabojstw", QuestType.KILL_USER, 5, 750, 0));
        this.quests.add(new Quest("Zdabadz 10 zabojstw", QuestType.KILL_USER, 10, 1450, 0));
        this.quests.add(new Quest("Zdabadz 15 zabojstw", QuestType.KILL_USER, 15, 1950, 0));
        this.quests.add(new Quest("Zdabadz 25 zabojstw", QuestType.KILL_USER, 25, 2500, 0));
        this.quests.add(new Quest("Zdabadz 50 zabojstw", QuestType.KILL_USER, 50, 3000, 0));

        this.questTypes = getQuestKinds();
    }

    public void checkQuest(User user, QuestType questType){
        this.quests.stream().filter(quest -> quest.getQuestType().equals(questType)).filter(quest -> quest.getAmount() == getAmountByQuest(user, questType)).forEach(quest -> {
            user.getUserStat().addCoins(quest.getReward());
            ChatUtil.sendMessage(user.asPlayer(), MessagesManager.playerQuestMessage.replace("{QUEST-NAME}", quest.getName()).replace("{QUEST-REWARD}", Integer.toString(quest.getReward())));
        });
    }

    public void checkTimeQuest(User user){
        this.quests.stream().filter(quest -> quest.getQuestType().equals(QuestType.SPENT_TIME)).filter(quest -> user.getUserStat().getOnlineTime() >= TimeUnit.HOURS.toMillis(quest.getAmount())).filter(quest -> user.getUserStat().getTimeAwardAmount() < quest.getLevel()).forEach(quest -> {
            user.getUserStat().setTimeAwardAmount(user.getUserStat().getTimeAwardAmount() + 1);
            user.getUserStat().addCoins(quest.getReward());
            ChatUtil.sendMessage(user.asPlayer(), MessagesManager.playerQuestMessage.replace("{QUEST-NAME}", quest.getName()).replace("{QUEST-REWARD}", Integer.toString(quest.getReward())));
        });
    }

    public void checkComebackQuest(User user){
        this.quests.stream().filter(quest -> quest.getQuestType().equals(QuestType.COMEBACK)).filter(quest -> quest.getAmount() == user.getUserStat().getComebackDaysInRow()).filter(quest -> user.getUserStat().getComebackAwardAmount() < quest.getLevel()).forEach(quest -> {
            user.getUserStat().setComebackAwardAmount(user.getUserStat().getComebackAwardAmount() + 1);
            user.getUserStat().addCoins(quest.getReward());
            ChatUtil.sendMessage(user.asPlayer(), MessagesManager.playerQuestMessage.replace("{QUEST-NAME}", quest.getName()).replace("{QUEST-REWARD}", Integer.toString(quest.getReward())));
        });
    }

    public List<Quest> getQuestsByType(QuestType questType){
        return this.quests.stream().filter(quest -> quest.getQuestType().equals(questType)).collect(Collectors.toList());
    }

    public boolean isDone(User user, Quest quest){
        if(quest.getQuestType().equals(QuestType.COMEBACK)){
            return user.getUserStat().getComebackAwardAmount() >= quest.getLevel();
        }
        if(quest.getQuestType().equals(QuestType.SPENT_TIME)){
            return user.getUserStat().getTimeAwardAmount() >= quest.getLevel();
        }
        return getAmountByQuest(user, quest.getQuestType()) >= quest.getAmount();
    }

    public int getAmountByQuest(User user, QuestType questType){
        if(questType.equals(QuestType.MINED_WOOD)){
            return user.getUserStat().getMinedWood();
        }
        if(questType.equals(QuestType.MINED_STONE)){
            return user.getUserStat().getMinedStone();
        }
        if(questType.equals(QuestType.CATCHED_FISH)){
            return user.getUserStat().getCatchedFishes();
        }
        if(questType.equals(QuestType.EXPLORE_GUILDS)){
            return user.getUserStat().getExploredGuilds().size();
        }
        if(questType.equals(QuestType.KILL_UNIQUE_USERS)){
            return user.getUserStat().getKilledUsers().size();
        }
        if(questType.equals(QuestType.KILL_USERS_WITH_RANK)){
            return user.getUserStat().getKilledWithRankUsers().size();
        }
        if(questType.equals(QuestType.TRAVELED_DISTANCE)){
            return user.getUserStat().getDistanceTraveled();
        }
        if(questType.equals(QuestType.OPENED_PREMIUMCASE)){
            return user.getUserStat().getOpenedPremiumCase();
        }
        if(questType.equals(QuestType.OPENED_COBBLEX)){
            return user.getUserStat().getOpenedCobblex();
        }
        if(questType.equals(QuestType.ASSISTS)){
            return user.getUserStat().getAssists();
        }
        if(questType.equals(QuestType.KILL_USER)){
            return user.getUserStat().getKills();
        }
        return 999;
    }

    private List<QuestType> getQuestKinds(){
        Set<QuestType> questTypes = new HashSet<>();
        List<QuestType> quests = new ArrayList<>();
        this.quests.stream().forEach(quest -> {
            if(!questTypes.contains(quest.getQuestType())){
                quests.add(quest.getQuestType());
                questTypes.add(quest.getQuestType());
            }
        });
        return quests;
    }

    public List<QuestType> getQuestTypes(){
        return this.questTypes;
    }
}
