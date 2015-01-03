package com.storemanager.components;

import com.storemanager.util.ButtonEnum;
import com.storemanager.util.StoreLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageButton extends JButton {
    private StoreLogger logger = StoreLogger.getInstance(ImageButton.class);
    private String command;

    public ImageButton(ButtonEnum info, ActionListener listener) {
        super(info.getName());
        this.addActionListener(listener);
        //this.setBorder(BorderFactory.createEmptyBorder());
        //this.setContentAreaFilled(false);
        this.command = info.getCommand();
        try {
            File img = new File(info.getImagePath());
            BufferedImage image = ImageIO.read(img);
            this.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public ImageButton(ButtonEnum info) {
        this(info, null);
    }

    public String getCommand() {
        return command;
    }
}
