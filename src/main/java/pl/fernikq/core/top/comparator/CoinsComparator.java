package pl.fernikq.core.top.comparator;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CoinsComparator implements Sortable<User> {

    private final CorePlugin plugin;
    private List<User> userList;
    private TopType topType;
    private TopKind topKind;
    private Comparator<User> userComparator;

    public CoinsComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.userList = new ArrayList<>();
        this.userComparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int i = Integer.compare(o2.getUserStat().getCoins(), o1.getUserStat().getCoins());
                if(i == 0){
                    if(o1.getName() == null){
                        return -1;
                    }
                    if(o2.getName() == null){
                        return 1;
                    }
                    i = o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        };
    }

    @Override
    public void sort() {
        this.userList.sort(this.userComparator);
    }

    @Override
    public User getObjectByPosition(int position){
        return this.userList.size() > position - 1 ? this.userList.get(position) : null;
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
        this.userList.add(user);
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
