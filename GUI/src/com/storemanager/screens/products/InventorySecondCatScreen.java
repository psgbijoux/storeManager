package com.storemanager.screens.products;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.*;
import com.storemanager.screens.sales.ImagePanel;
import com.storemanager.service.*;
import com.storemanager.util.ButtonEnum;
import com.storemanager.util.ButtonSizeEnum;
import com.storemanager.util.Message;
import com.storemanager.util.ServiceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class InventorySecondCatScreen extends AbstractPanel {
    private Window baseWindow;
    private JButton close, clearSearch, start, generate;
    private JTextField searchCode;
    private Product currentProduct;
    private JComboBox<Role> categoryBox;

    private ImagePanel imagePanel;

    public InventorySecondCatScreen(Window baseWindow) {
        this.baseWindow = baseWindow;

        this.setSize(800, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(670, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        JLabel label = new JLabel("2: Bare Code:");
        Font newLabelFont = new Font(label.getFont().getName(), label.getFont().getStyle(), 20);
        label.setFont(newLabelFont);
        label.setBounds(190, 20, 250, 35);
        this.add(label);

        searchCode = new JTextField();
        searchCode.setEnabled(false);
        searchCode.setBounds(300, 20, 250, 35);
        Font newFieldFont = new Font(searchCode.getFont().getName(), searchCode.getFont().getStyle(), 30);
        searchCode.setFont(newFieldFont);
        searchCode.addKeyListener(this);
        this.add(searchCode);

        clearSearch = new ImageButton(ButtonEnum.CLEAR_SEARCH, this);
        clearSearch.setLocation(570, 18);
        clearSearch.setSize(80, 40);
        this.add(clearSearch);

        start = new ImageButton(ButtonEnum.START, this);
        start.setLocation(300, 520);
        start.setText("Begin Inventory");
        start.setSize(ButtonSizeEnum.LARGE_LONG.getSize());
        this.add(start);

        generate = new ImageButton(ButtonEnum.GENERATE, this);
        generate.setLocation(300, 520);
        generate.setText("Inventory Results");
        generate.setSize(ButtonSizeEnum.LARGE_LONG.getSize());
        generate.setVisible(false);
        this.add(generate);

        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
        java.util.List<Category> categoryList = null;
        try {
            categoryList = service.getAllCategories();
        } catch (ServiceException e) {
            Message.show(e);
        }
        categoryBox = new JComboBox(categoryList.toArray());
        categoryBox.setBounds(30, 520, 240, 40);
        categoryBox.addActionListener(this);
        this.add(categoryBox);

        this.repaint();
        this.validate();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        } else if (trigger.getCommand().equals(ButtonEnum.CLEAR_SEARCH.getCommand())) {
            searchCode.setText("");
            currentProduct = null;
            searchCode.requestFocus();
        } else if (trigger.getCommand().equals(ButtonEnum.START.getCommand())) {
            InventoryService inventoryService = ServiceLocator.getService(ServiceName.INVENTORY_SERVICE);
            try {
                inventoryService.clear("INVENTORY_CAT2");
                searchCode.setEnabled(true);
                start.setVisible(false);
                generate.setVisible(true);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        } else if (trigger.getCommand().equals(ButtonEnum.GENERATE.getCommand())) {
            InventoryService inventoryService = ServiceLocator.getService(ServiceName.INVENTORY_SERVICE);
            Category category = (Category) categoryBox.getSelectedItem();
            try {
                inventoryService.generateInventoryReport("INVENTORY_CAT2", category);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        }
        this.repaint();
        this.validate();
    }

    public void keyReleased(KeyEvent e) {
        ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        Product product = null;
        try {
            String code = searchCode.getText();
            if (code.length() == 8 || code.length() > 12 || e.getKeyCode() == KeyEvent.VK_ENTER) {
                product = service.getProductByBarCode(code);
            }
        } catch (ServiceException e1) {
            Message.show(e1);
        }
        if (product != null) {
            Toolkit.getDefaultToolkit().beep();
            //display product
            Image image = Toolkit.getDefaultToolkit().createImage(product.getImage());
            int x = 750;
            int y = 400;
            if (imagePanel != null) {
                this.remove(imagePanel);
            }
            imagePanel = new ImagePanel(image, x, y);
            imagePanel.setBounds(25, 80, x, y);
            this.add(imagePanel);

            //insert in inventory table
            InventoryService inventoryService = ServiceLocator.getService(ServiceName.INVENTORY_SERVICE);
            InventorySec inventory = new InventorySec(product);
            try {
                inventoryService.addInventorySecond(inventory);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
            //clear search code
            searchCode.setText("");
        } else {
            if (imagePanel != null && searchCode.getText().length() > 0) {
                this.remove(imagePanel);
            }
        }
        this.repaint();
        this.validate();
    }
}
