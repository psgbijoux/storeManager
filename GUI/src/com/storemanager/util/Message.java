package com.storemanager.util;

import javax.swing.*;

public class Message {
    public static final int ERROR = JOptionPane.ERROR_MESSAGE;
    public static final int INFO = JOptionPane.INFORMATION_MESSAGE;


    public static void show(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, Message.ERROR);
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", Message.ERROR);
    }

    public static void show(ServiceException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", Message.ERROR);
    }
}
