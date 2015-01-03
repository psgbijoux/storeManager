package com.storemanager.components;

import com.storemanager.models.Menu;
import com.storemanager.util.StoreLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageMenu extends JMenu {
    private StoreLogger logger = StoreLogger.getInstance(ImageMenu.class);

    public ImageMenu(Menu menuEnum) {
        super(menuEnum.getName());
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        if (menuEnum.getMnemonic() > 0) {
            this.setMnemonic(menuEnum.getMnemonic());
        }
        if (menuEnum.getImagePath() != null) {
            try {
                File img = new File(menuEnum.getImagePath());
                BufferedImage image = ImageIO.read(img);
                this.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
