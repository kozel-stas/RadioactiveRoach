package com.roach.utils;

public class StringUtils {

    // end - exclude
    // start - include
    public static String[] concat(String[] list, int start, int end) {
        String[] result = new String[end - start];
        System.arraycopy(list, start, result, 0, end - start);
        return result;
    }

}
