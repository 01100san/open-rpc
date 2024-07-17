package com.zhl.utils;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 17:43
 */
public class StringUtil {
    public static boolean isBlank(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
