package com.sep490.gshop.utils;

import java.util.List;

public class StringFormatUtil {
    private StringFormatUtil() {
    }

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append(item).append(";");
        }
        // Remove the last semicolon if it exists
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    public static List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        String[] items = str.split(";");
        return List.of(items);
    }
}
