package com.github.sbcharr.util;

public class Utils {
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            String str = (String) obj;
            return str.isEmpty();
        }

        return false;
    }
}
