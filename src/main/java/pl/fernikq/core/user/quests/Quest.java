package pl.fernikq.core.user.quests;

public class Quest {

    private String name;
    private QuestType questType;
    private int amount;
    private int reward;
    private int level;

    public Quest(String name, QuestType questType, int amount, int reward, int level) {
        this.name = name;
        this.questType = questType;
        this.amount = amount;
        this.reward = reward;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
