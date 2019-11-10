package pl.fernikq.core.util;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String formatDouble(double d) {
        return String.format("%.2f", d);
    }
    public static boolean isInt(String string){
        try{
            Integer.parseInt(string);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isFloat(String string){
        try{
            Float.parseFloat(string);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
