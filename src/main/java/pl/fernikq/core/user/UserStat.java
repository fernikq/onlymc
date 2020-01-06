package pl.fernikq.core.user;

import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private int logouts;
    private int distanceTraveled;
    private long spentTime;
    private long joinTime;

    //QUEST
    private int comebackDay;
    private int comebackDaysInRow;
    private int minedWood;
    private Set<String> exploredGuilds;
    private Set<User> killedUsers;
    private Set<User> killedWithRankUsers;
    private int catchedFishes;
    private int timeAwardAmount;
    private int comebackAwardAmount;

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
        this.logouts = 0;
        this.distanceTraveled = 0;
        this.spentTime = 0L;
        this.comebackDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        this.comebackDaysInRow = 1;
        this.comebackAwardAmount = 0;
        this.minedWood = 0;
        this.catchedFishes = 0;
        this.timeAwardAmount = 0;
        this.killedUsers = new HashSet<>();
        this.killedWithRankUsers = new HashSet<>();
        this.exploredGuilds = new HashSet<>();
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
            this.distanceTraveled = rs.getInt("distanceTraveled");
            this.logouts = rs.getInt("logouts");
            this.spentTime = rs.getLong("spentTime");
            this.joinTime = 0L;
            this.comebackDay = rs.getInt("comebackDay");
            this.comebackDaysInRow = rs.getInt("comebackDaysInRow");
            this.minedWood = rs.getInt("minedWood");
            this.catchedFishes = rs.getInt("catchedFishes");
            this.timeAwardAmount = rs.getInt("timeAwardAmount");
            this.comebackAwardAmount = rs.getInt("comebackAwardAmount");
            this.killedUsers = new HashSet<>();
            this.killedWithRankUsers = new HashSet<>();
            this.exploredGuilds = new HashSet<>();
            if(spentTime > TimeUnit.DAYS.toMillis(100)){
                this.spentTime = 0L;
            }
            user.setUserStat(this);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public int getComebackAwardAmount() {
        return comebackAwardAmount;
    }

    public void setComebackAwardAmount(int comebackAwardAmount) {
        this.comebackAwardAmount = comebackAwardAmount;
    }

    public int getComebackDay() {
        return comebackDay;
    }

    public void setComebackDay(int comebackDay) {
        this.comebackDay = comebackDay;
    }

    public int getComebackDaysInRow() {
        return comebackDaysInRow;
    }

    public void setComebackDaysInRow(int comebackDaysInRow) {
        this.comebackDaysInRow = comebackDaysInRow;
    }

    public int getMinedWood() {
        return minedWood;
    }

    public void setMinedWood(int minedWood) {
        this.minedWood = minedWood;
    }

    public Set<String> getExploredGuilds() {
        return exploredGuilds;
    }

    public void setExploredGuilds(Set<String> exploredGuilds) {
        this.exploredGuilds = exploredGuilds;
    }

    public Set<User> getKilledUsers() {
        return killedUsers;
    }

    public void setKilledUsers(Set<User> killedUsers) {
        this.killedUsers = killedUsers;
    }

    public Set<User> getKilledWithRankUsers() {
        return killedWithRankUsers;
    }

    public void setKilledWithRankUsers(Set<User> killedWithRankUsers) {
        this.killedWithRankUsers = killedWithRankUsers;
    }

    public int getCatchedFishes() {
        return catchedFishes;
    }

    public void setCatchedFishes(int catchedFishes) {
        this.catchedFishes = catchedFishes;
    }

    public int getTimeAwardAmount() {
        return timeAwardAmount;
    }

    public void setTimeAwardAmount(int timeAwardAmount) {
        this.timeAwardAmount = timeAwardAmount;
    }

    public long getOnlineTime(){
        return this.joinTime > 0 ? this.spentTime + (System.currentTimeMillis() - this.joinTime) : this.spentTime;
    }

    public long getSpentTime() {
        return spentTime;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public int getLogouts() {
        return logouts;
    }

    public void setLogouts(int logouts) {
        this.logouts = logouts;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
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

    public int getStoneToNextLevel(){
        return this.level * 260 - this.miningExperience;
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
