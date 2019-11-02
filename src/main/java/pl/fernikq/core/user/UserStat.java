package pl.fernikq.core.user;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStat {

    private int coins;
    private int level;
    private int depositePearls;
    private int depositeApples;
    private int depositeEnchantedApples;

    public UserStat(User user){
        this.coins = 0;
        this.level = 1;
        this.depositeApples = 0;
        this.depositeEnchantedApples = 0;
        this.depositePearls = 0;
        user.setUserStat(this);
    }

    public UserStat(User user, ResultSet rs){
        try {
            this.coins = rs.getInt("coins");
            this.level = rs.getInt("level");
            this.depositePearls = rs.getInt("depositePearls");
            this.depositeApples = rs.getInt("depositeApples");
            this.depositeEnchantedApples = rs.getInt("depositeEnchantedApples");
            user.setUserStat(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void removeCoins(int amount){
        this.coins -= amount;
    }

    public void addLevel(int amount) {
        this.level += amount;
    }

    public void removeLevel(int amount) {
        this.level -= amount;
    }

    public void addDepositeApples(int amount) {
        this.depositeApples += amount;
    }

    public void removeDepositeApples(int amount) {
        this.depositeApples -= amount;
    }

    public void addDepositePearls(int amount) {
        this.depositePearls += amount;
    }

    public void removeDepositePearls(int amount) {
        this.depositePearls -= amount;
    }

    public void addDepositeEnchantedApples(int amount) {
        this.depositeEnchantedApples += amount;
    }

    public void removeDepositeEnchantedApples(int amount) {
        this.depositeEnchantedApples -= amount;
    }

    public int getCoins() {
        return coins;
    }

    public int getLevel() {
        return level;
    }

    public int getDepositePearls() {
        return depositePearls;
    }

    public int getDepositeApples() {
        return depositeApples;
    }

    public int getDepositeEnchantedApples() {
        return depositeEnchantedApples;
    }
}
