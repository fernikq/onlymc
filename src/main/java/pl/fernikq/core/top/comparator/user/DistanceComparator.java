package pl.fernikq.core.top.comparator.user;

import org.bukkit.Material;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;
import pl.fernikq.core.util.StringUtil;

import java.util.*;

public class DistanceComparator implements Sortable<User> {

    private final CorePlugin plugin;
    private List<User> sortedList;
    private Set<User> users;
    private TopType topType;
    private TopKind topKind;
    private boolean isSorted;
    private Comparator<User> userComparator;

    public DistanceComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.sortedList = new ArrayList<>();
        this.users = new HashSet<>();
        this.userComparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int i = Integer.compare(o2.getUserStat().getDistanceTraveled(), o1.getUserStat().getDistanceTraveled());
                if(i == 0){
                    if(o1.getName() == null){
                        return -1;
                    }
                    if(o2.getName() == null){
                        return 1;
                    }
                    i = o1.getName().compareTo(o2.getName());
                }
                return i;
            }
        };
    }

    @Override
    public InventoryGUI getInventory(User object) {
        InventoryGUI inventoryGUI = new InventoryGUI("&8[ {c}&l"+this.topType.getGuiTitle()+" &8]", 6, true);
        for(int i = 0; i < 45; i++){
            User topUser = this.getObjectByPosition(i);
            if(topUser == null){
                break;
            }
            inventoryGUI.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{n}Pozycja {c}"+(i + 1))).setSkullOwner(topUser.getName()).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gracz&8: {c}"+topUser.getName(), "&8>> {n}Przebyta odleglosc&8: {c}"+ StringUtil.formatDistance(topUser.getUserStat().getDistanceTraveled())))).toItemStack());
        }
        inventoryGUI.setItem(49, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{n}Twoja pozycja&8: {c}"+(getPositionByObject(object)+1))).setSkullOwner(object.getName())
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Przebyta odleglosc&8: {c}"+StringUtil.formatDistance(object.getUserStat().getDistanceTraveled())))).toItemStack());
        return inventoryGUI;
    }

    @Override
    public void sort() {
        if(this.users.isEmpty()){
            return;
        }
        this.sortedList = new ArrayList<>(this.users);
        this.sortedList.sort(this.userComparator);
    }

    @Override
    public User getObjectByPosition(int position){
        List<User> sortedList = new ArrayList<>(this.sortedList);
        return sortedList.size() > position ? sortedList.get(position) : null;
    }

    @Override
    public int getPositionByObject(User user){
        return new ArrayList<>(this.sortedList).indexOf(user);
    }

    @Override
    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
    }

    @Override
    public void addObject(User user){
        this.users.add(user);
    }

    @Override
    public void removeObject(User user){
        this.users.remove(user);
    }

    @Override
    public TopType getTopType() {
        return topType;
    }

    @Override
    public TopKind getTopKind() {
        return topKind;
    }
}
