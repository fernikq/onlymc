package pl.fernikq.core.guild.treasure;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildTreasure {

    private Guild guild;
    private int coins;
    private ItemStack[] items;
    private Inventory inventory;
    private int level;

    public GuildTreasure(Guild guild){
        this.guild = guild;
        this.coins = 0;
        this.items = new ItemStack[27];
        this.level = 0;
        this.inventory = Bukkit.createInventory(null, getSizeByLevel(), ChatUtil.fixColor("&8[ {c}&lSkarbiec gildii &8]"));
    }

    public GuildTreasure(Guild guild, ResultSet resultSet){
        try {
            this.guild = guild;
            this.coins = resultSet.getInt("coins");
            this.items = SerializationUtil.itemStackFromString(resultSet.getString("items"));
            this.level = resultSet.getInt("level");
            this.inventory = Bukkit.createInventory(null, getSizeByLevel(), ChatUtil.fixColor("&8[ {c}&lSkarbiec gildii &8]"));
            this.inventory.setContents(this.items);
            this.guild.setTreasure(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCostByLevel(){
        if(level == 0){
            return ConfigManager.guildTreasureSizeFirstLevelCost;
        }
        else if(level == 1){
            return ConfigManager.guildTreasureSizeSecondLevelCost;
        }
        else if(level == 2){
            return ConfigManager.guildTreasureSizeThirdLevelCost;
        }
        return 1000000;
    }

    private int getSizeByLevel(){
        if(level == 0){
            return 27;
        }
        else if(level == 1){
            return 36;
        }
        else if(level == 2){
            return 45;
        }
        return 54;
    }
}
