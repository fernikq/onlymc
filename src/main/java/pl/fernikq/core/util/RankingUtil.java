package pl.fernikq.core.util;

import pl.fernikq.core.config.ConfigManager;

public class RankingUtil {

    public static int calculatePoints(int deathPoints, int killPoints){
        double percent = deathPoints * 0.07D;
        double points;
        if (killPoints <= deathPoints) {
            double amount = (deathPoints - killPoints) / killPoints + 1.0D;
            points = Math.round(percent * amount);
            killPoints += points;
        }else {
            double amount = (killPoints - deathPoints) / deathPoints + 1.0D;
            points =  Math.round(percent / amount);
            killPoints += points;
        }
        if(points > ConfigManager.maxAmountOfPointsByKilling) points = ConfigManager.maxAmountOfPointsByKilling;
        if(points < 0) points = 0;
        return (int)points;
    }
}
