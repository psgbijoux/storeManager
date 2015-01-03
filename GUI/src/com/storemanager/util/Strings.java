package com.storemanager.util;

public class Strings {
    public static final String EMPTY = "";

    public static boolean isEmpty(String string) {
        if (string != null && string.trim().equals(EMPTY)) {
            return true;
        }
        return false;
    }
}
