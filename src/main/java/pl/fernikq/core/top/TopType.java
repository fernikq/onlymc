package pl.fernikq.core.top;

import org.bukkit.Material;
import pl.fernikq.core.config.ConfigManager;

public enum TopType {

    USER_COINS("Topka Monet", Material.DOUBLE_PLANT),
    USER_ASSISTS("Topka Asyst", Material.IRON_SWORD),
    USER_COBBLEX("Topka Cobblex", Material.MOSSY_COBBLESTONE),
    USER_DEATHS("Topka Smierci", Material.SKULL_ITEM),
    USER_DISTANCE("Topka Dystansu", Material.GOLD_BOOTS),
    USER_KILLS("Topka Zabojstw", Material.DIAMOND_SWORD),
    USER_LOGOUTS("Topka Logoutow", Material.IRON_DOOR),
    USER_POINTS("Topka Punktow", Material.GOLD_SWORD),
    USER_CASE("Topka Premiumcase", Material.CHEST),
    USER_STONE("Topka Kamienia", Material.STONE),
    USER_TIME("Topka Czasu", Material.WATCH),
    GUILD_ASSISTS("Topka Asyst", Material.IRON_SWORD),
    GUILD_DEATHS("Topka Smierci", Material.SKULL_ITEM),
    GUILD_KILLS("Topka Zabojstw", Material.DIAMOND_SWORD),
    GUILD_LOGOUTS("Topka Logoutow", Material.IRON_DOOR),
    GUILD_POINTS("Topka Punktow", Material.GOLD_SWORD),
    GUILD_COINS("Topka Monet", Material.DOUBLE_PLANT);

    private String name;
    private Material material;
    private String guiTitle;

    private TopType(String name, Material material){
        this.name = name;
        this.material = material;
        this.guiTitle = name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getGuiTitle() {
        return guiTitle;
    }
}
