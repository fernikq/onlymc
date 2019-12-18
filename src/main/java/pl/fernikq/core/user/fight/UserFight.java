package pl.fernikq.core.user.fight;

import pl.fernikq.core.region.Region;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.TimeUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserFight {

    private User user;
    private Map<User, Damage> damageMap;
    private User lastAttacker;
    private long lastAttackTime;
    private Map<User, Long> lastKilled;
    private Comparator<Damage> damageComparator;

    public UserFight(User user){
        this.user = user;
        this.lastAttacker = null;
        this.lastAttackTime = 0L;
        this.damageMap = new HashMap<>();
        this.lastKilled = new HashMap<>();
        this.damageComparator = new Comparator<Damage>() {
            @Override
            public int compare(Damage o1, Damage o2) {
                int i = Double.compare(o2.getDamage(), o1.getDamage());
                if(i == 0){
                    if(o1.getUser().getName() == null){
                        return -1;
                    }
                    if(o2.getUser().getName() == null){
                        return 1;
                    }
                    i = o1.getUser().getName().compareTo(o2.getUser().getName());
                }
                return i;
            }
        };
    }

    public boolean wasKilledLastTime(User user){
        return this.lastKilled.containsKey(user) && this.lastKilled.get(user) > System.currentTimeMillis();
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public boolean isDuringFight(){
        return this.lastAttackTime >= System.currentTimeMillis();
    }

    public boolean wasDuringFight(int seconds){
        return this.lastAttackTime > System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(seconds);
    }

    public int getTimeToEnd(){
        if(this.lastAttackTime - System.currentTimeMillis() < 1000){
            return 0;
        }
        return (int) TimeUnit.MILLISECONDS.toSeconds(this.lastAttackTime - System.currentTimeMillis());
    }

    public User getUser() {
        return user;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttacker(User lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    public Damage getDamageByUser(User user){
        return this.damageMap.get(user);
    }

    public Map<User, Damage> getDamageMap() {
        return damageMap;
    }

    public void removeFight(){
        this.damageMap.clear();
        this.lastAttackTime = 0L;
        this.lastAttacker = null;
    }

    public List<Damage> getDamageList(){
        List<Damage> damageList = new ArrayList<>(this.damageMap.values());
        damageList.sort(this.damageComparator);
        return damageList;
    }

    public User getLastAttacker() {
        return lastAttacker;
    }
}
