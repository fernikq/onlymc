package pl.fernikq.core.kit;

import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.user.UserGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Kit {

    private ItemStack item;
    private String name;
    private List<KitItem> items;
    private long time;
    private UserGroup group;
    private boolean canRankHigher;

    public Kit(ItemStack itemStack, String name, long time, UserGroup userGroup, boolean canRankHigher){
        this.item = itemStack;
        this.name = name;
        this.time = time;
        this.group = userGroup;
        this.items = new ArrayList<>();
        this.canRankHigher = canRankHigher;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KitItem> getItems() {
        return new ArrayList<>(this.items);
    }

    public void addItem(KitItem kitItem){
        this.items.add(kitItem);
    }

    public void setItems(List<KitItem> items) {
        this.items = items;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public boolean canRankHigher() {
        return canRankHigher;
    }

    public void setCanRankHigher(boolean canRankHigher) {
        this.canRankHigher = canRankHigher;
    }
}
