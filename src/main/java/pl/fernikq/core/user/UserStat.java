package pl.fernikq.core.user;

import pl.fernikq.core.config.ConfigManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStat {

    private int coins;
    private int level;
    private int depositePearls;
    private int depositeApples;
    private int depositeEnchantedApples;
    private int miningExperience;
    private int minedStone;
    private int openedCobblex;
    private int openedPremiumCase;
    private int coinsFromStone;
    private long turboDropTime;
    private long turboExpTime;
    private int points;
    private int kills;
    private int deaths;
    private int assists;

    public UserStat(User user){
        this.coins = 0;
        this.level = 1;
        this.depositeApples = 0;
        this.depositeEnchantedApples = 0;
        this.depositePearls = 0;
        this.miningExperience = 0;
        this.minedStone = 0;
        this.openedCobblex = 0;
        this.openedPremiumCase = 0;
        this.coinsFromStone = 0;
        this.turboDropTime = 0L;
        this.turboExpTime = 0L;
        this.points = ConfigManager.playerStartPoints;
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        user.setUserStat(this);
    }

    public UserStat(User user, ResultSet rs){
        try {
            this.coins = rs.getInt("coins");
            this.level = rs.getInt("level");
            this.depositePearls = rs.getInt("depositePearls");
            this.depositeApples = rs.getInt("depositeApples");
            this.depositeEnchantedApples = rs.getInt("depositeEnchantedApples");
            this.miningExperience = rs.getInt("miningExperience");
            this.minedStone = rs.getInt("minedStone");
            this.openedCobblex = rs.getInt("openedCobblex");
            this.openedPremiumCase = rs.getInt("openedPremiumCase");
            this.coinsFromStone = rs.getInt("coinsFromStone");
            this.turboDropTime = rs.getLong("turboDropTime");
            this.turboExpTime = rs.getLong("turboExpTime");
            this.points = rs.getInt("points");
            this.kills = rs.getInt("kills");
            this.deaths = rs.getInt("deaths");
            this.assists = rs.getInt("assists");
            user.setUserStat(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTurboDropTime(long turboDropTime) {
        this.turboDropTime = turboDropTime;
    }

    public void setTurboExpTime(long turboExpTime) {
        this.turboExpTime = turboExpTime;
    }

    public void addCoinsFromStone(int amount){
        this.coinsFromStone += amount;
    }

    public void addOpenedPremiumCase(int amount){
        this.openedPremiumCase += amount;
    }

    public void addOpenedCobblex(int amount){
        this.openedCobblex += amount;
    }

    public void addMinedStone(int amount){
        this.minedStone += amount;
    }

    public void addMiningExperience(int amount){
        this.miningExperience += amount;
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

    public void recalculateMiningExperience(){
        this.miningExperience = (this.level - 1) * 260;
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

    public void setPoints(int points) {
        this.points = points;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getPoints() {
        return points;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
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

    public int getMiningExperience() {
        return miningExperience;
    }

    public int getMinedStone() {
        return minedStone;
    }

    public int getOpenedCobblex() {
        return openedCobblex;
    }

    public int getOpenedPremiumCase() {
        return openedPremiumCase;
    }

    public int getCoinsFromStone() {
        return coinsFromStone;
    }

    public boolean isTurboDrop(){
        return this.turboDropTime > System.currentTimeMillis() || ConfigManager.turboDropTime > System.currentTimeMillis();
    }

    public boolean isTurboExp(){
        return this.turboExpTime > System.currentTimeMillis() || ConfigManager.turboExpTime > System.currentTimeMillis();
    }

    public long getTurboDropTime() {
        return turboDropTime;
    }

    public long getTurboExpTime() {
        return turboExpTime;
    }
}
