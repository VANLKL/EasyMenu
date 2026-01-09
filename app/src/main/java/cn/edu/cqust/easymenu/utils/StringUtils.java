package cn.edu.cqust.easymenu.utils;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String safeTrim(String str) {
        return str == null ? "" : str.trim();
    }

    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }
}
