package pl.fernikq.core.user.quests;

import org.bukkit.Material;
import pl.fernikq.core.CoreAPI;

public enum QuestType {

    MINED_WOOD("Zadania Drwala", Material.LOG),
    MINED_STONE("Zadania Kopacza", Material.STONE),
    COMEBACK("Zadania Powracajacego", Material.IRON_DOOR),
    CATCHED_FISH("Zadania Rybaka", Material.RAW_FISH),
    SPENT_TIME("Zadania Nolife'a", Material.WATCH),
    EXPLORE_GUILDS("Zadania Odkrywcy", Material.ENDER_PORTAL_FRAME),
    KILL_UNIQUE_USERS("Zadania Kolekcjonera", Material.SKULL_ITEM),
    KILL_USERS_WITH_RANK("Zadania Pay2Win", Material.DIAMOND_SWORD),
    TRAVELED_DISTANCE("Zadania Wedrowca", Material.GOLD_BOOTS),
    OPENED_PREMIUMCASE("Zadania Otwieracza", CoreAPI.getPlugin().getDropManager().getPremiumCaseItem().getType()),
    OPENED_COBBLEX("Zadania Otwieracza", CoreAPI.getPlugin().getDropManager().getCobblexItem().getType()),
    ASSISTS("Zadania Asystujacego", Material.IRON_SWORD),
    KILL_USER("Zadania Zabojcy", Material.GOLD_SWORD);

    private Material material;
    private String name;

    private QuestType(String name, Material material){
        this.material = material;
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }
}
