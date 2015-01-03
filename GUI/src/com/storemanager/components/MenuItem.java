package com.storemanager.components;

import com.storemanager.models.Menu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuItem extends JMenuItem {
    private String command;

    public MenuItem(Menu info, ActionListener listener) {
        super("     " + info.getName());
        if (info.getImagePath() != null) {
            Icon icon = new ImageIcon(info.getImagePath());
            this.setIcon(icon);
        }
        if (info.getMnemonic() > 0) {
            this.setMnemonic(info.getMnemonic());
        }
        this.addActionListener(listener);
        this.command = info.getCommand();
    }

    public MenuItem(Menu info) {
        this(info, null);
    }

    public String getCommand() {
        return command;
    }
}
