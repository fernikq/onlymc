package pl.fernikq.core.user;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStat {

    private int coins;
    private int level;

    public UserStat(User user){
        this.coins = 0;
        this.level = 1;
        user.setUserStat(this);
    }

    public UserStat(User user, ResultSet rs){
        try {
            this.coins = rs.getInt("coins");
            this.level = rs.getInt("level");
            user.setUserStat(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins){
        this.coins -= coins;
    }

    public void addLevel(int level) {
        this.level += level;
    }

    public void removeLevel(int level) {
        this.level -= level;
    }

    public int getCoins() {
        return coins;
    }

    public int getLevel() {
        return level;
    }
}
