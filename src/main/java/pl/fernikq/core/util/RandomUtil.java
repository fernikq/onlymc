package pl.fernikq.core.util;

import java.util.Random;

public class RandomUtil {

    private static final Random rand = new Random();

    public static int getRandInt(int min, int max){
        return rand.nextInt(max - min + 1) + min;
    }

    public static Double getRandDouble(double min, double max){
        return Double.valueOf(rand.nextDouble() * (max - min) + min);
    }
    public static boolean getChance(double chance){
        return (chance >= 100.0D) || (chance >= getRandDouble(0.0D, 100.0D).doubleValue());
    }
}
