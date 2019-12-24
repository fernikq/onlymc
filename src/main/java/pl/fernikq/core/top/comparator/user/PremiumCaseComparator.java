package pl.fernikq.core.top.comparator.user;

import org.bukkit.Material;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PremiumCaseComparator implements Sortable<User> {

    private final CorePlugin plugin;
    private List<User> userList;
    private TopType topType;
    private TopKind topKind;
    private Comparator<User> userComparator;

    public PremiumCaseComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.userList = new ArrayList<>();
        this.userComparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int i = Integer.compare(o2.getUserStat().getOpenedPremiumCase(), o1.getUserStat().getOpenedPremiumCase());
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
        sort();
        InventoryGUI inventoryGUI = new InventoryGUI("{c}&lTOPKA "+this.plugin.getDropManager().getPremiumCaseNameInGUI().toUpperCase(), 6, true);
        for(int i = 0; i < 45; i++){
            User topUser = this.getObjectByPosition(i);
            if(topUser == null){
                break;
            }
            inventoryGUI.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{n}Pozycja {c}"+(i + 1))).setSkullOwner(topUser.getName()).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gracz&8: {c}"+topUser.getName(), "&8>> {n}Otworzyl {c}"+topUser.getUserStat().getOpenedPremiumCase()+" {n}"+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow"))).toItemStack());
        }
        inventoryGUI.setItem(49, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setName(ChatUtil.fixColor("{n}Twoja pozycja&8: {c}"+(getPositionByObject(object)+1))).setSkullOwner(object.getName())
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Otworzyles {c}"+object.getUserStat().getOpenedPremiumCase()+" {n}"+this.plugin.getDropManager().getPremiumCaseNameInGUI()+"'ow"))).toItemStack());
        return inventoryGUI;
    }

    @Override
    public void sort() {
        this.userList.sort(this.userComparator);
    }

    @Override
    public User getObjectByPosition(int position){
        return this.userList.size() > position ? this.userList.get(position) : null;
    }

    @Override
    public int getPositionByObject(User user){
        return this.userList.indexOf(user);
    }

    @Override
    public void addObject(User user){
        if(this.userList.contains(user)){
            return;
        }
        this.userList.add(user);
    }

    @Override
    public void removeObject(User user){
        if(!this.userList.contains(user)){
            return;
        }
        this.userList.remove(user);
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
