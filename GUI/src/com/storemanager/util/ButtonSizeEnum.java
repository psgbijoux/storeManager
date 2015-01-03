package com.storemanager.util;

import java.awt.*;

public enum ButtonSizeEnum {
    DEFAULT(new Dimension(110, 40)),
    LONG(new Dimension(120, 40)),
    LARGE(new Dimension(150, 70)),
    LARGE_LONG(new Dimension(200, 60)),
    EXTRA_LARGE(new Dimension(165, 80));
    private Dimension size;

    private ButtonSizeEnum(Dimension size) {
        this.size = size;
    }

    public Dimension getSize() {
        return size;
    }
}
