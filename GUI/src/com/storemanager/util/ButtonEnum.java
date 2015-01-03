package com.storemanager.util;

public enum ButtonEnum {
    OPEN("Open", "GUI/resources/buttons32/open.png", "btn_open"),
    BACK("Back", "GUI/resources/buttons32/back.png", "btn_back"),
    SAVE("Save", "GUI/resources/buttons32/save.png", "btn_save"),
    ADD("Add", "GUI/resources/buttons32/add-new.png", "btn_add"),
    CANCEL("Cancel", "GUI/resources/buttons32/cancel.png", "btn_cancel"),
    CLOSE("Close", "GUI/resources/buttons32/close.png", "btn_close"),
    EDIT("Edit", "GUI/resources/buttons32/edit.png", "btn_edit"),
    DELETE("Delete", "GUI/resources/buttons32/delete.png", "btn_delete"),
    HISTORY("History", "GUI/resources/buttons32/delete.png", "btn_history"),
    BARCODEHISTORY("Barcode", "GUI/resources/buttons32/open.png", "btn_barcode_history"),
    LOGIN("Login", "GUI/resources/buttons32/login_btn.png", "btn_login"),
    GENERATE("Generate", "GUI/resources/buttons48/generate.png", "btn_generate"),
    CLEAR_SEARCH("", "GUI/resources/buttons48/clearSearch.png", "btn_clear_search"),
    START("START", "GUI/resources/buttons48/start.png", "btn_start"),
    PREVIEW("PreView", "GUI/resources/buttons32/preview.png", "btn_preview"),
    CHECKOUT("Checkout", "GUI/resources/buttons64/checkout.png", "btn_checkout"),
    CHECKCARD("Card Check", "GUI/resources/buttons64/checkout.png", "btn_checkout_card");

    private String imagePath;
    private String name;
    private String command;

    private ButtonEnum(String name, String imagePath, String command) {
        this.imagePath = imagePath;
        this.name = name;
        this.command = command;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }
}
