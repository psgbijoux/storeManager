package com.storemanager.screens.file;

import com.storemanager.components.Window;
import com.storemanager.util.ImagePath;
import com.storemanager.util.StoreLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class NotAutorized extends JPanel {
    private StoreLogger logger = StoreLogger.getInstance(NotAutorized.class);
    private Window baseWindow;

    public NotAutorized(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(400, 300);
        this.setBackground(Color.white);
        JLabel message = new JLabel("Not Authorized! Please login using valid credentials.");
        this.setLayout(null);
        message.setBounds(50, 270, 300, 30);
        this.add(message);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            File img = new File(ImagePath.NOT_AUTHORIZED.getImagePath());
            BufferedImage image = ImageIO.read(img);
            g.drawImage(image, 80, 20, null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
