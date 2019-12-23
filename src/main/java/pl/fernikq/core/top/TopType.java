package pl.fernikq.core.top;

import org.bukkit.Material;
import pl.fernikq.core.config.ConfigManager;

public enum TopType {

    USER_COINS("TOPKA MONET", Material.DOUBLE_PLANT),
    USER_ASSISTS("TOPKA ASYST", Material.IRON_SWORD),
    USER_COBBLEX("TOPKA COBBLEX", Material.MOSSY_COBBLESTONE),
    USER_DEATHS("TOPKA SMIERCI", Material.SKULL_ITEM),
    USER_DISTANCE("TOPKA DYSTANSU", Material.GOLD_BOOTS),
    USER_KILLS("TOPKA ZABOJSTW", Material.DIAMOND_SWORD),
    USER_LOGOUTS("TOPKA LOGOUTOW", Material.IRON_DOOR),
    USER_POINTS("TOPKA PUNKTOW", Material.GOLD_SWORD),
    USER_CASE("TOPKA PREMIUMCASE", Material.CHEST),
    USER_STONE("TOPKA KAMIENIA", Material.STONE),
    USER_TIME("TOPKA CZASU", Material.WATCH),
    GUILD_ASSISTS("TOPKA ASYST", Material.IRON_SWORD),
    GUILD_DEATHS("TOPKA SMIERCI", Material.SKULL_ITEM),
    GUILD_KILLS("TOPKA ZABOJSTW", Material.DIAMOND_SWORD),
    GUILD_LOGOUTS("TOPKA LOGOUTOW", Material.IRON_DOOR),
    GUILD_POINTS("TOPKA PUNKTOW", Material.GOLD_SWORD);

    private String name;
    private Material material;

    private TopType(String name, Material material){
        this.name = name;
        this.material = material;
    }
}
