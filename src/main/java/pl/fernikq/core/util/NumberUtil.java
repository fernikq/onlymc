package pl.fernikq.core.util;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String formatDouble(double d) {
        DecimalFormat df2 = new DecimalFormat(".##");
        return df2.format(d);
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
