package pl.fernikq.core.user.enderchest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemUtil;
import pl.fernikq.core.util.SerializationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Enderchest {

    private User user;
    private int level;
    private ItemStack[] items;
    private Inventory inventory;
    private User userEnderchest;

    public Enderchest(User user){
        this.user = user;
        this.level = 0;
        this.inventory = Bukkit.createInventory(null, getSizeByLevel(), ChatUtil.fixColor("&8[ {c}&lEnderchest &8]"));
        if(this.inventory.getContents() != null){
            this.items = this.inventory.getContents();
        }
    }

    public Enderchest(User user, ResultSet resultSet){
        try {
            this.user = user;
            this.items = SerializationUtil.itemStackFromString(resultSet.getString("enderchestItems"));
            this.level = resultSet.getInt("enderchestLevel");
            this.inventory = Bukkit.createInventory(null, getSizeByLevel(), ChatUtil.fixColor("&8[ {c}&lEnderchest &8]"));
            if(this.items != null){
                this.inventory.setContents(this.items);
            }
            user.setEnderchest(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void recalculateEnderchest(){
        Bukkit.getOnlinePlayers().forEach(o -> {
            if(o.getOpenInventory() != null && o.getOpenInventory().getTopInventory().equals(this.inventory)){
                o.closeInventory();
                ChatUtil.sendMessage(o, MessagesManager.error("Enderchest zostal zamkniety, poniewaz wykonana zostala jakas akcja!"));
            }
        });
        this.inventory = Bukkit.createInventory(null, getSizeByLevel(), ChatUtil.fixColor("&8[ {c}&lEnderchest &8]"));
        if(this.items != null){
            this.inventory.setContents(this.items);
        }
    }

    public User getUserEnderchest() {
        return userEnderchest;
    }

    public void setUserEnderchest(User userEnderchest) {
        this.userEnderchest = userEnderchest;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ItemStack[] getItems() {
        return items.clone();
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

    public int getCostByLevel(){
        if(level == 0){
            return ConfigManager.enderchestSizeFirstLevelCost;
        }
        else if(level == 1){
            return ConfigManager.enderchestSizeSecondLevelCost;
        }
        else if(level == 2){
            return ConfigManager.enderchestSizeThirdLevelCost;
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
