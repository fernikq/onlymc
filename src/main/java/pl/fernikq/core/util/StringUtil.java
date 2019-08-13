package pl.fernikq.core.util;

public class StringUtil {

    public static String replace(String text, String searchString, int replacement) {
        return replace(text, searchString, Integer.toString(replacement));
    }

    public static String replace(String text, String searchString, long replacement) {
        return replace(text, searchString, Long.toString(replacement));
    }

    public static String replace(String text, String searchString, boolean replacement) {
        return replace(text, searchString, Boolean.toString(replacement));
    }

    public static String replace(String text, String searchString, String replacement) {
        if ((text == null) || (text.isEmpty()) || (searchString.isEmpty())) {
            return text;
        }
        if (replacement == null) {
            replacement = "";
        }
        int start = 0;
        int max = -1;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= (max < 0 ? 16 : max > 64 ? 64 : max);
        StringBuilder sb = new StringBuilder(text.length() + increase);
        while (end != -1) {
            sb.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            max--;
            if (max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        sb.append(text.substring(start));
        return sb.toString();
    }
}
