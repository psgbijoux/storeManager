package com.storemanager.util;

public class FieldValidator {
    public static String validateStringField(String name, String value) {
        if (Strings.isEmpty(value)) {
            return "Field " + name + " is required.\n";
        }
        return Strings.EMPTY;
    }

    public static String validateDoubleField(String name, String value) {
        if (!Strings.isEmpty(value)) {
            try {
                double dValue = Double.parseDouble(value);
                if (dValue >= 0.0) {
                    return Strings.EMPTY;
                }
            } catch (NumberFormatException nfe) {
                return "Invalid value for field " + name + ".\n";
            }
        }
        return "Field " + name + " is required.\n";
    }

    public static String validateIntField(String name, String value) {
        if (!Strings.isEmpty(value)) {
            try {
                int iValue = Integer.parseInt(value);
                if (iValue >= 0) {
                    return Strings.EMPTY;
                }
            } catch (NumberFormatException nfe) {
                return "Invalid value for field " + name + ".\n";
            }
        }
        return "Field " + name + " is required.\n";
    }


}
