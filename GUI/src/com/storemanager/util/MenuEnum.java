package com.storemanager.util;

import java.awt.event.KeyEvent;

public enum MenuEnum {
    LOGIN("login_app"),
    LOGOUT("logout_app"),
    EXIT("exit_app", KeyEvent.VK_Q),
    MANAGE_ROLES("manage_roles"),
    MANAGE_MENUS("manage_menus"),
    MANAGE_ROLE_MENU("manage_role_menu_xref"),
    MANAGE_USERS("manage_users"),
    MANAGE_SETTINGS("manage_settings"),
    MANAGE_CATEGORIES("manage_categories"),
    MANAGE_PRODUCTS("manage_products"),
    PRODUCT_HISTORY("product_history"),
    MANAGE_INVENTORY("manage_inventory"),
    MANAGE_SUPPLY("manage_supply"),
    MANAGE_SALES("manage_sales"),
    MANAGE_ZREPORT("manage_zreport"),
    MANAGE_STOCK("manage_stock"),
    MANAGE_OUT_OF_STOCK("manage_out_of_stock"),
    MANAGE_SMART_SUPPLY("manage_smart_supply"),
    MANAGE_SALE_REPORTS("manage_sale_reports");

    private String command;
    private int mnemonic;

    private MenuEnum(String command, int mnemonic) {
        this.command = command;
        this.mnemonic = mnemonic;
    }

    private MenuEnum(String command) {
        this(command, -1);
    }

    public String getCommand() {
        return command;
    }

    public int getMnemonic() {
        return mnemonic;
    }
}
