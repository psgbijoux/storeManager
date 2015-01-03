package com.storemanager.screens.sales;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Product;
import com.storemanager.models.ScreenProduct;
import com.storemanager.models.Settings;
import com.storemanager.service.*;
import com.storemanager.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class SalesScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(SalesScreen.class);
    private Window baseWindow;

    private int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getSize().width - 40;
    private int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getSize().height - 100;

    private JButton close, edit, cancel, update, delete, checkout, checkoutCard, clearSearch;
    private JTextField searchCode, quantity, name, code, unitPrice, price, description, stock, discount;
    private JLabel quantityLabel, unitPriceLabel, discountLabel;
    private JPanel searchPanel, searchPanelBtn, editPanel, imagePanel, actionPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;

    private boolean isUpdateProduct = false;
    private int updateProductIndex = -1;

    private Product currentProduct;
    private SaleService service;

    private JLabel totalLabel;

    public SalesScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(screenWidth, screenHeight);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(screenWidth - 130, screenHeight - 60);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        service = ServiceLocator.getService(ServiceName.SALE_SERVICE);
        service.clearList();

        createSearchPanel();
        createActionsPanel();
        createEditPanel();
        createProductList();

        clearSearch.doClick();
    }

    //UI Comp - creeaza LeftPanel + RightPanel
    private void createEditPanel() {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(30, 20, screenWidth - 430, screenHeight - 340);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setEditable(false);
        name.setBounds(100, 20, 150, 25);
        editPanel.add(name);

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setBounds(20, 50, 100, 25);
        editPanel.add(codeLabel);

        code = new JTextField();
        code.setEditable(false);
        code.setBounds(100, 50, 150, 25);
        editPanel.add(code);

        quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 80, 100, 25);
        editPanel.add(quantityLabel);

        quantity = new JTextField();
        quantity.setBounds(100, 80, 150, 25);
        quantity.getDocument().addDocumentListener(new QuantityListener());
        editPanel.add(quantity);

        unitPriceLabel = new JLabel("Unit Price:");
        unitPriceLabel.setBounds(20, 110, 100, 25);
        editPanel.add(unitPriceLabel);

        unitPrice = new JTextField();
        unitPrice.setEditable(false);
        unitPrice.setBounds(100, 110, 150, 25);
        editPanel.add(unitPrice);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, 140, 100, 25);
        editPanel.add(descLabel);

        description = new JTextField();
        description.setEditable(false);
        description.setBounds(100, 140, 150, 25);
        editPanel.add(description);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 170, 100, 25);
        editPanel.add(priceLabel);

        price = new JTextField();
        price.setEditable(false);
        price.setBounds(100, 170, 150, 25);
        editPanel.add(price);

        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setBounds(20, 200, 100, 25);
        editPanel.add(stockLabel);

        stock = new JTextField();
        stock.setEditable(false);
        stock.setBounds(100, 200, 150, 25);
        editPanel.add(stock);

        discountLabel = new JLabel("Discount: (%)");
        discountLabel.setBounds(20, 230, 100, 25);
        editPanel.add(discountLabel);

        discount = new JTextField();
        discount.setEditable(true);
        discount.setBounds(100, 230, 150, 25);
        editPanel.add(discount);

        int x = screenWidth - 750;
        int y = screenHeight - 450;
        try {
            File img = new File(ImagePath.SALE_BKGND.getImagePath());
            BufferedImage image = ImageIO.read(img);
            imagePanel = new ImagePanel(image, x, y);
            imagePanel.setBounds(280, 20, x, y);
            editPanel.add(imagePanel);
        } catch (Exception e) {
            Message.showError(e.getMessage());
        }

        update = new ImageButton(ButtonEnum.SAVE, this);
        update.setLocation(20, 270);
        update.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(update);
        update.setVisible(false);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(140, 270);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);
        cancel.setVisible(false);

        this.add(editPanel);
        this.repaint();
        this.validate();
    }
    //UI Comp - creeaza pagina principala
    private void createSearchPanel() {
        searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(screenWidth - 370, 20, 350, 60);
        searchPanel.setBackground(Color.lightGray);

        searchCode = new JTextField();
        searchCode.setBounds(15, 10, 230, 35);
        Font newFieldFont = new Font(searchCode.getFont().getName(), searchCode.getFont().getStyle(), 30);
        searchCode.setFont(newFieldFont);
        searchCode.addKeyListener(this);
        searchPanel.add(searchCode);

        clearSearch = new ImageButton(ButtonEnum.CLEAR_SEARCH, this);
        clearSearch.setLocation(260, 8);
        clearSearch.setSize(80, 40);
        searchPanel.add(clearSearch);

        this.add(searchPanel);

        //btns
        searchPanelBtn = new JPanel();
        searchPanelBtn.setLayout(null);
        searchPanelBtn.setBounds(screenWidth - 370, 80, 350, 300);
        searchPanelBtn.setBackground(Color.lightGray);

        GridLayout experimentLayout = new GridLayout(4, 3, 5, 5);

        SearchPanelListener listener = new SearchPanelListener(this);
        searchPanelBtn.setLayout(experimentLayout);

        for (int i = 1; i <= 9; i++) {
            JButton btn = new JButton(Integer.toString(i));
            Font newButtonFont = new Font(btn.getFont().getName(), btn.getFont().getStyle(), 30);
            btn.setFont(newButtonFont);
            btn.addActionListener(listener);
            searchPanelBtn.add(btn);
        }
        JButton btn = new JButton("C");
        Font newButtonFont = new Font(btn.getFont().getName(), btn.getFont().getStyle(), 30);
        btn.setFont(newButtonFont);
        btn.addActionListener(listener);
        searchPanelBtn.add(btn);
        btn = new JButton(Integer.toString(0));
        btn.setFont(newButtonFont);
        btn.addActionListener(listener);
        searchPanelBtn.add(btn);
        btn = new JButton("ADD");
        btn.setFont(newButtonFont);
        btn.addActionListener(listener);
        searchPanelBtn.add(btn);
        this.add(searchPanelBtn);
        this.repaint();
        this.validate();
    }
    //UI Comp - create EDIT.DELETE.CHECKOUT.CHECKCARD buttons
    private void createActionsPanel() {
        actionPanel = new JPanel();
        actionPanel.setLayout(null);
        actionPanel.setBounds(screenWidth - 370, 400, 350, 200);
        actionPanel.setBackground(Color.lightGray);

        ActionPanelListener listener = new ActionPanelListener();
        edit = new ImageButton(ButtonEnum.EDIT, this);
        edit.setLocation(20, 20);
        edit.addActionListener(listener);
        edit.setSize(ButtonSizeEnum.LARGE.getSize());
        actionPanel.add(edit);

        delete = new ImageButton(ButtonEnum.DELETE, this);
        delete.setLocation(180, 20);
        delete.setSize(ButtonSizeEnum.LARGE.getSize());
        delete.addActionListener(listener);
        actionPanel.add(delete);

        checkout = new ImageButton(ButtonEnum.CHECKOUT, this);
        checkout.setLocation(5, 100);
        checkout.setSize(ButtonSizeEnum.EXTRA_LARGE.getSize());
        checkout.addActionListener(listener);
        actionPanel.add(checkout);

        checkoutCard = new ImageButton(ButtonEnum.CHECKCARD, this);
        checkoutCard.setLocation(180, 100);
        checkoutCard.setSize(ButtonSizeEnum.EXTRA_LARGE.getSize());
        checkoutCard.addActionListener(listener);
        actionPanel.add(checkoutCard);

        this.add(actionPanel);
        this.repaint();
        this.validate();
    }
    //UI Comp - create product list
    private void createProductList() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("#");
        tableModel.addColumn("Product");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Unit Price");
        tableModel.addColumn("Discount");
        tableModel.addColumn("Price");

        table = new JTable(tableModel);
        table.setFont(new Font("Serif", Font.BOLD, 20));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Serif", Font.BOLD, 20));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(40);

        int tableWidth = screenWidth - 430;
        int idCollWidth = 30;
        int priceCollWidth = 150;
        int unitPriceCollWidth = 150;
        int discountCollWidth = 150;
        int qtyCollWidth = 100;
        int nameCollWidth = tableWidth - idCollWidth - priceCollWidth - qtyCollWidth - unitPriceCollWidth - 200;
        table.getColumnModel().getColumn(0).setPreferredWidth(idCollWidth);
        table.getColumnModel().getColumn(1).setPreferredWidth(nameCollWidth);
        table.getColumnModel().getColumn(2).setPreferredWidth(qtyCollWidth);
        table.getColumnModel().getColumn(3).setPreferredWidth(unitPriceCollWidth);
        table.getColumnModel().getColumn(4).setPreferredWidth(discountCollWidth);
        table.getColumnModel().getColumn(5).setPreferredWidth(priceCollWidth);

        if (scrollPane != null) {
            this.remove(scrollPane);
        }

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(30, screenHeight - 300, tableWidth, 230);
        this.add(scrollPane);

        totalLabel = new JLabel();
        totalLabel.setBounds(screenWidth - 680, screenHeight - 60, 300, 35);
        Font newFont = new Font(totalLabel.getFont().getName(), totalLabel.getFont().getStyle(), 30);
        totalLabel.setFont(newFont);
        this.add(totalLabel);
        this.setTotal(new BigDecimal(0.0));
        this.repaint();
    }

    private void setTotal(BigDecimal priceSum) {
        totalLabel.setText("Total: " + priceSum + " Ron");
    }
    //add item to ORDER ENTRIES FOR SALE
    public void addItem(Product product, int quantity, double weight, int discount) {
        ScreenProduct newItem = null;
        if (product.isOther()) {
            newItem = new ScreenProduct(product, quantity, discount);
        } else {
            newItem = new ScreenProduct(product, weight, discount);
        }
        ScreenProduct productOnScreen = null;
        for (ScreenProduct screenProduct : service.getList()) {
            if (screenProduct.getProduct().getId() == newItem.getProduct().getId()
                    && screenProduct.getDiscount() == newItem.getDiscount()) {
                productOnScreen = screenProduct;
                break;
            }
        }
        if (productOnScreen != null) {
            if (product.isOther()) {
                int newQuantity = productOnScreen.getQuantity() + newItem.getQuantity();
                BigDecimal newPrice = productOnScreen.getProduct().getPrice().multiply(new BigDecimal(newQuantity));
                newPrice = newPrice.subtract(newPrice.multiply(new BigDecimal(discount)).multiply(new BigDecimal(0.01)));
                productOnScreen.setQuantity(newQuantity);
                productOnScreen.setPrice(newPrice);
                productOnScreen.setDiscount(discount);
            }
        } else {
            service.add(newItem);
        }
        quantityLabel.setText("Quantity:");
        unitPriceLabel.setText("Unit price:");
        discountLabel.setText("Discount:");
        refreshProductList();
    }

    private void editProduct(ScreenProduct product) {
        editProduct(product, false);
    }

    private void editProduct(ScreenProduct product, boolean isUpdate) {
        if (product != null) {
            name.setText(product.getProduct().getName());
            code.setText(product.getProduct().getCode());
            price.setText(product.getPrice().toString());
            if (product.getProduct().isOther()) {
                quantity.setText(Integer.toString(product.getQuantity()));
                quantityLabel.setText("Quantity:");
                unitPriceLabel.setText("Unit price:");
                quantity.setEditable(true);
            } else {
                quantityLabel.setText("Weight:");
                unitPriceLabel.setText("Gold GR price:");
                quantity.setEditable(false);

                BigDecimal weight = new BigDecimal(product.getProduct().getWeight());
                weight = weight.setScale(2, RoundingMode.HALF_UP);
                quantity.setText(weight.toString());
            }
            unitPrice.setText(product.getProduct().getPrice().toString());
            stock.setText("" + product.getProduct().getQuantity());
            description.setText(product.getProduct().getDescription());
            discount.setText(Integer.toString(product.getDiscount()));

            Image image = Toolkit.getDefaultToolkit().createImage(product.getProduct().getImage());
            int x = screenWidth - 750;
            int y = screenHeight - 450;
            imagePanel = new ImagePanel(image, x, y);
            imagePanel.setBounds(280, 20, x, y);
            editPanel.add(imagePanel);
            if (isUpdate) {
                update.setVisible(true);
                cancel.setVisible(true);
            }
        } else {
            name.setText("");
            code.setText("");
            price.setText("");
            unitPrice.setText("");
            quantity.setText("");
            description.setText("");
            stock.setText("");
            discount.setText("");
            if (imagePanel != null) {
                editPanel.remove(imagePanel);
            }
            update.setVisible(false);
            cancel.setVisible(false);

            int x = screenWidth - 750;
            int y = screenHeight - 450;
            try {
                File img = new File(ImagePath.SALE_BKGND.getImagePath());
                BufferedImage image = ImageIO.read(img);
                imagePanel = new ImagePanel(image, x, y);
                imagePanel.setBounds(280, 20, x, y);
                editPanel.add(imagePanel);
            } catch (Exception e) {
                Message.showError(e.getMessage());
            }

            editPanel.repaint();
            editPanel.validate();
            //todo current product made NULL
            currentProduct = null;
        }
        this.repaint();
        this.validate();
    }
    //remove item from product list
    public void removeSelectedProduct() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            int response = JOptionPane.showConfirmDialog(null, "Do you want to remove the selected product?", "Remove product", JOptionPane.YES_NO_OPTION);
            if (response == 0) {
                service.remove(index);
                editProduct(null);
                refreshProductList();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a product first.");
        }
    }

    private void refreshProductList() {
        int size = tableModel.getRowCount();
        for (int i = 0; i < size; i++) {
            tableModel.removeRow(0);
        }
        int rowCount = 0;
        for (ScreenProduct product : service.getList()) {
            rowCount++;
            Object[] row;
            if(product.getProduct().isOther()) {
                row = new Object[]{
                        rowCount,
                        product.getProduct().getName(),
                        product.getQuantity(),
                        product.getProduct().getPrice(),
                        product.getDiscount() + "%",
                        product.getPrice()};
            } else {
                row = new Object[]{
                        rowCount,
                        product.getProduct().getName(),
                        product.getWeight(),
                        product.getProduct().getPrice(),
                        product.getDiscount() + "%",
                        product.getPrice()};
            }
            tableModel.addRow(row);
        }
        calculateTotal();
    }

    private void setSearchCode(String code) {
        this.searchCode.setText(code);
    }
    private String getSearchCode() {
        return searchCode.getText();
    }
    private boolean isProduct() {
        return currentProduct != null;
    }
    private void setQuantity(String qty) {
        this.quantity.setText(qty);
    }
    private String getQuantity() {
        return quantity.getText();
    }
    private void setDiscount(String discount) {
        this.discount.setText(discount);
    }
    private String getDiscount() {
        return discount.getText();
    }
    private void calculateTotal() {
        this.setTotal(service.getTotal());
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        } else if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            editProduct(null);
            isUpdateProduct = false;
            searchCode.requestFocus();
        } else if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            ScreenProduct screenProduct = service.get(updateProductIndex);
            service.remove(updateProductIndex);
            if (screenProduct.getProduct().isOther()) {
                addItem(screenProduct.getProduct(), Integer.parseInt(getQuantity()), 0.0, Integer.parseInt(getDiscount()));
            } else {
                addItem(screenProduct.getProduct(), 1, screenProduct.getWeight(), Integer.parseInt(getDiscount()));
            }
            editProduct(null);
            isUpdateProduct = false;
            refreshProductList();
            searchCode.requestFocus();
        } else if (trigger.getCommand().equals(ButtonEnum.CLEAR_SEARCH.getCommand())) {
            searchCode.setText("");
            editProduct(null);
            isUpdateProduct = false;
            searchCode.requestFocus();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (isUpdateProduct) {
            return;
        }
        ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        Product product = null;
        try {
            String code = this.getSearchCode();
            if (code.length() == 8 || code.length() == 13 || e.getKeyCode() == KeyEvent.VK_ENTER) {
                product = service.getProductByBarCode(code);
            }
        } catch (ServiceException e1) {
            Message.show(e1);
        }
        this.currentProduct = product;
        ScreenProduct screenProduct = null;
        if (product != null) {
            if (product.isOther()) {
                screenProduct = new ScreenProduct(product, 1, 0);
            } else {
                SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
                Settings goldPriceSetting = null;
                try {
                    goldPriceSetting = settingsService.loadSettings(SettingsEnum.GOLD_GR_PRICE);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                double grPrice = 0.0;
                if (goldPriceSetting == null) {
                    Message.showError("Gold GR price is not set.");
                } else {
                    try {
                        grPrice = goldPriceSetting.getDoubleValue();
                    } catch (Exception se) {
                        Message.showError(se.getMessage());
                    }
                }
                BigDecimal unitPrice = new BigDecimal(grPrice);
                unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
                product.setPrice(unitPrice);
                screenProduct = new ScreenProduct(product, product.getWeight(), 0);
            }
        }
        editProduct(screenProduct);
    }

    private class SearchPanelListener implements ActionListener {

        private SalesScreen screen;

        public SearchPanelListener(SalesScreen screen) {
            this.screen = screen;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("ADD")) {
                if (screen.isProduct()) {
                    if (currentProduct.isOther()) {
                        int quantity;
                        try {
                            quantity = Integer.parseInt(screen.getQuantity());
                        } catch (Exception nfe) {
                            quantity = 1;
                        }
                        int discount;
                        try {
                            discount = Integer.parseInt(screen.getDiscount());
                        } catch (Exception nfe) {
                            discount = 0;
                        }
                        int stockValue = Integer.parseInt((!Strings.isEmpty(stock.getText()) ? stock.getText() : "0"));
                        if (quantity > stockValue) {
                            Message.showError("Quantity to sell can not exceed stock quantity.");
                            return;
                        }
                        //todo add discount as param
                        screen.addItem(currentProduct, quantity, 0.0, discount);
                    } else {
                        int quantity = 1;
                        int stockValue = Integer.parseInt((!Strings.isEmpty(stock.getText()) ? stock.getText() : "0"));
                        int discount;
                        try {
                            discount = Integer.parseInt(screen.getDiscount());
                        } catch (Exception nfe) {
                            discount = 0;
                        }
                        if (quantity > stockValue) {
                            Message.showError("Quantity to sell can not exceed stock quantity.");
                            return;
                        }
                        double weight;
                        try {
                            weight = Double.parseDouble(screen.getQuantity());
                        } catch (Exception nfe) {
                            weight = 0.0;
                        }
                        screen.addItem(currentProduct, quantity, weight, discount);
                    }
                    screen.setSearchCode("");
                    currentProduct = null;
                    editProduct(null);
                    searchCode.requestFocus();
                }
            } else if (e.getActionCommand().equals("C")) {
                if (currentProduct != null && currentProduct.isOther()) {
                    screen.setQuantity("");
                }
                searchCode.requestFocus();
            } else {
                if (currentProduct == null) {
                    String currentCode = searchCode.getText();
                    searchCode.setText(currentCode + e.getActionCommand());
                } else if (currentProduct.isOther()) {
                    String actualQty = screen.getQuantity();
                    screen.setQuantity(actualQty + e.getActionCommand());
                }
            }
        }
    }

    private class ActionPanelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ImageButton button = (ImageButton) e.getSource();
            if (button.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    ScreenProduct screenProduct = service.get(index);
                    editProduct(screenProduct, true);
                    currentProduct = screenProduct.getProduct();
                    isUpdateProduct = true;
                    updateProductIndex = index;
                } else {
                    Message.showError("Please select a product first.");
                }
            } else if (button.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
                removeSelectedProduct();
                searchCode.requestFocus();
            } else if (button.getCommand().equals(ButtonEnum.CHECKOUT.getCommand())) {
                try {
                    if (service.getList().size() > 0) {
                        service.createSale(baseWindow.getLoggedUser(), new Date(), true);
                    } else {
                        Message.showError("Sell list empty.");
                    }
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                editProduct(null);
                refreshProductList();
                searchCode.requestFocus();
            } else if (button.getCommand().equals(ButtonEnum.CHECKCARD.getCommand())) {
                try {
                    if (service.getList().size() > 0) {
                        service.createSale(baseWindow.getLoggedUser(), new Date(), false);
                    } else {
                        Message.showError("Sell list empty.");
                    }
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                editProduct(null);
                refreshProductList();
                searchCode.requestFocus();
            }
        }
    }

    private class QuantityListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            calcTotalPrice();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calcTotalPrice();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void calcTotalPrice() {
            if (Strings.isEmpty(unitPrice.getText())) {
                price.setText("");
                return;
            }
            String quantityStr = quantity.getText();
            if (Strings.isEmpty(quantityStr)) {
                quantityStr = "0";
            }
            int stockValue = Integer.parseInt((!Strings.isEmpty(stock.getText()) ? stock.getText() : "0"));

            if (currentProduct.isOther() && Integer.parseInt(quantityStr) > stockValue) {
                quantityStr = Integer.toString(stockValue);
                Message.showError("Quantity to sell can not exceed stock quantity.\nStock: " + stockValue);
            }

            BigDecimal qty = new BigDecimal(quantityStr);
            BigDecimal priceVal = new BigDecimal(unitPrice.getText());
            priceVal = priceVal.multiply(qty);
            priceVal = priceVal.setScale(2, RoundingMode.HALF_UP);
            price.setText(priceVal.toString());
        }
    }
}
