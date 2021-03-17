package nl.siegmann.ttcsv.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Various string utility functions.
 *
 * Similar to apache commons stringutils, but reduces the number of dependencies.
 */
public class StringUtils {

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isBlank(CharSequence value) {
        if (isEmpty(value)) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List<String> split(String s, char separator) {
        List<String> result = new ArrayList<>();
        if (isEmpty(s)) {
            return result;
        }
        int start = 0;
        int end = s.indexOf(separator);
        while (end >= 0) {
            result.add(s.substring(start, end));
            start = end + 1;
            end = s.indexOf(separator, start);
        }
        result.add(s.substring(start));
        return result;
    }

    public static List<String> split(String s, String separator) {
        List<String> result = new ArrayList<>();
        if (isEmpty(s)) {
            return result;
        }
        int start = 0;
        int end = s.indexOf(separator);
        while (end >= 0) {
            result.add(s.substring(start, end));
            start = end + separator.length();
            end = s.indexOf(separator, start);
        }
        result.add(s.substring(start));
        return result;
    }
}
