package com.storemanager.util;

public enum ImagePath {
    LOGIN_LOGO("GUI/resources/login_logo.png"),
    BUSY("GUI/resources/busy.png"),
    NOT_AUTHORIZED("GUI/resources/not_authorized.png"),
    SALE_BKGND("GUI/resources/sales_bkgnd.png");

    private String imagePath;

    private ImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
