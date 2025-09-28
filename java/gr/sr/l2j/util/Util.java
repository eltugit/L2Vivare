package gr.sr.l2j.util;

public final class Util {
    public static boolean isAlphaNumeric(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }
}


