package com.storemanager.listeners;

import com.storemanager.components.MenuItem;
import com.storemanager.components.Window;
import com.storemanager.screens.categories.CategoryScreen;
import com.storemanager.screens.file.LoginScreen;
import com.storemanager.screens.file.NotAutorized;
import com.storemanager.screens.products.*;
import com.storemanager.screens.reports.DiscountReportScreen;
import com.storemanager.screens.reports.ReportsScreen;
import com.storemanager.screens.sales.SalesScreen;
import com.storemanager.screens.settings.*;
import com.storemanager.service.*;
import com.storemanager.util.MenuEnum;
import com.storemanager.util.Message;
import com.storemanager.util.ServiceException;
import com.storemanager.util.processes.ProductsUpdateProcess;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuListener implements ActionListener {
    private Window baseWindow;

    public MenuListener(Window baseWindow) {
        this.baseWindow = baseWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem menuItem = (MenuItem) e.getSource();
        if (menuItem.getCommand().equals(MenuEnum.EXIT.getCommand())) {
            System.exit(0);
        }
        if (menuItem.getCommand().equals(MenuEnum.LOGIN.getCommand())) {
            JPanel panel = new LoginScreen(baseWindow);
            baseWindow.setContent(panel);
        }
        if (menuItem.getCommand().equals(MenuEnum.LOGOUT.getCommand())) {
            baseWindow.setLoggedUser(null);
            baseWindow.setContent(new NotAutorized(baseWindow));
        }
        //settings
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_ROLES.getCommand())) {
            baseWindow.setContent(new RoleScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_MENUS.getCommand())) {
            baseWindow.setContent(new MenuScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_ROLE_MENU.getCommand())) {
            baseWindow.setContent(new RoleMenuScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_USERS.getCommand())) {
            baseWindow.setContent(new UserScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_SETTINGS.getCommand())) {
            baseWindow.setContent(new SettingsScreen(baseWindow));
        }

        //app
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_CATEGORIES.getCommand())) {
            baseWindow.setContent(new CategoryScreen(baseWindow));
        }

        if (menuItem.getCommand().equals(MenuEnum.MANAGE_PRODUCTS.getCommand())) {
            baseWindow.setContent(new ProductScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.PRODUCT_HISTORY.getCommand())) {
            baseWindow.setContent(new ProductHistoryScreen(baseWindow));
        }

//        if (menuItem.getCommand().equals(MenuEnum.MANAGE_INVENTORY.getCommand())) {
//            baseWindow.setContent(new InventoryScreen(baseWindow));
//        }
//        if (menuItem.getCommand().equals(MenuEnum.MANAGE_INVENTORY.getCommand())) {
//            baseWindow.setContent(new InventoryScreen(baseWindow));
//        }
//        if (menuItem.getCommand().equals(MenuEnum.MANAGE_INVENTORY.getCommand())) {
//            baseWindow.setContent(new InventoryScreen(baseWindow));
//        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_INVENTORY_FIRST.getCommand())) {
            baseWindow.setContent(new InventoryFirstCatScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_INVENTORY_SECOND.getCommand())) {
            baseWindow.setContent(new InventorySecondCatScreen(baseWindow));
        }

        if (menuItem.getCommand().equals(MenuEnum.INVENTORY_REPORT_GOLD.getCommand())) {
            InventoryService service = ServiceLocator.getService(ServiceName.INVENTORY_SERVICE);
            try {
                service.generateGoldInventoryReport();
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        }
        if (menuItem.getCommand().equals(MenuEnum.INVENTORY_REPORT_PRODUCTS.getCommand())) {
            InventoryService service = ServiceLocator.getService(ServiceName.INVENTORY_SERVICE);
            try {
                service.generateProductsInventoryReport();
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        }

        if (menuItem.getCommand().equals(MenuEnum.MANAGE_SUPPLY.getCommand())) {
            baseWindow.setContent(new SupplyScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_SALES.getCommand())) {
            baseWindow.setContent(new SalesScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_ZREPORT.getCommand())) {
            baseWindow.setContent(new ZReportScreen(baseWindow));
        }

        if (menuItem.getCommand().equals(MenuEnum.MANAGE_LABELS.getCommand())) {
            baseWindow.setContent(new LabelsScreen(baseWindow));
        }

        if (menuItem.getCommand().equals(MenuEnum.MANAGE_OUT_OF_STOCK.getCommand())) {
            baseWindow.setContent(new OutOfStockScreen(baseWindow));
        }
        if(menuItem.getCommand().equals(MenuEnum.MANAGE_SMART_SUPPLY.getCommand())) {
            baseWindow.setContent(new SmartSupplyScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_STOCK.getCommand())) {
            StockService service = ServiceLocator.getService(ServiceName.STOCK_SERVICE);
            try {
                service.generateStockReport();
            } catch (ServiceException e1) {
                Message.show(e1);
            }

        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_SALE_REPORTS.getCommand())) {
            baseWindow.setContent(new ReportsScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.MANAGE_DISCOUNT_REPORTS.getCommand())) {
            baseWindow.setContent(new DiscountReportScreen(baseWindow));
        }
        if (menuItem.getCommand().equals(MenuEnum.UPDATE_WATCH_DESCRIPTION.getCommand())) {
            ProductsUpdateProcess process = new ProductsUpdateProcess();
            process.startProcess();
        }
    }
}
