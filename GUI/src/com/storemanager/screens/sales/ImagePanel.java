package com.storemanager.screens.sales;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image image;
    private Image scaledImage;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private int x = 0;
    private int y = 0;

    public ImagePanel(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.setBackground(Color.lightGray);
        setScaledImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scaledImage != null) {
            int xCenter = x / 2 - scaledImage.getWidth(null) / 2;
            int yCenter = y / 2 - scaledImage.getHeight(null) / 2;
            g.drawImage(scaledImage, xCenter, yCenter, this);
        }
    }

    private void setScaledImage() {
        if (image != null) {
            float iw = imageWidth;
            float ih = imageHeight;
            float pw = x;   //panel width
            float ph = y;  //panel height

            if (pw < iw || ph < ih) {
                if ((pw / ph) > (iw / ih)) {
                    iw = -1;
                    ih = ph;
                } else {
                    iw = pw;
                    ih = -1;
                }
                if (iw == 0) {
                    iw = -1;
                }
                if (ih == 0) {
                    ih = -1;
                }
                scaledImage = image.getScaledInstance(new Float(iw).intValue(), new Float(ih).intValue(), Image.SCALE_DEFAULT);
            } else {
                scaledImage = image;
            }
        }
    }
}
