package com.storemanager.screens.products;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.screens.sales.ImagePanel;
import com.storemanager.service.ProductService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class SmartSupplyScreen extends AbstractPanel implements ItemListener{

    private StoreLogger logger = StoreLogger.getInstance(ProductScreen.class);
    private Window baseWindow;
    private JButton close, print;
    private JPanel editPanel, imagePanel;
    private JTextField name, code, price, quantity, description, printCount, alertValue, bareCodeValue, bcSearch;
    private JCheckBox alertFlag;
    private JRadioButton code8, code12, code13, addOperation, removeOperation;
    private int barcodeLength = 13;
    private int quantityAddition = 1;

    private final static String UPDATE = "UPDATE";

    public SmartSupplyScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(1200, 660);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(1060, 610);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        //search by barcode
        JLabel barCodeLabel = new JLabel("Barcode:");
        barCodeLabel.setLocation(20, 20);
        barCodeLabel.setSize(60, 20);
        this.add(barCodeLabel);

        ButtonGroup buttonGroupCode = new ButtonGroup();
        code8 = new JRadioButton("8");
        code8.setBounds(80, 20, 50, 20);
        code8.addActionListener(this);
        code8.setVisible(true);
        buttonGroupCode.add(code8);
        this.add(code8);
        code12 = new JRadioButton("12");
        code12.setBounds(125, 20, 50, 20);
        code12.addActionListener(this);
        code12.setVisible(true);
        buttonGroupCode.add(code12);
        this.add(code12);
        code13 = new JRadioButton("13");
        code13.setBounds(170, 20, 50, 20);
        code13.addActionListener(this);
        code13.setVisible(true);
        code13.setSelected(true);
        buttonGroupCode.add(code13);
        this.add(code13);

        bcSearch = new JTextField();
        bcSearch.setLocation(250, 20);
        bcSearch.setSize(400, 50);
        bcSearch.addKeyListener(this);
        bcSearch.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 30));
        this.add(bcSearch);
        bcSearch.requestFocusInWindow();

        JLabel operationLabel = new JLabel("Operation:");
        operationLabel.setLocation(20, 48);
        operationLabel.setSize(100, 25);
        this.add(operationLabel);

        ButtonGroup buttonGroupOperation = new ButtonGroup();
        addOperation = new JRadioButton("Add");
        addOperation.setBounds(85, 50, 60, 20);
        addOperation.addActionListener(this);
        addOperation.setSelected(true);
        buttonGroupOperation.add(addOperation);
        this.add(addOperation);
        removeOperation= new JRadioButton("Remove");
        removeOperation.setBounds(137, 50, 110, 20);
        removeOperation.addActionListener(this);
        buttonGroupOperation.add(removeOperation);
        this.add(removeOperation);
        this.repaint();
        this.validate();
    }

    private void edit(Product product) {

        if (editPanel != null)
            this.remove(editPanel);

        this.repaint();
        this.validate();

        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(20, 90, 1160, 510);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 150, 35);
        nameLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setBounds(200, 20, 250, 35);
        name.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        name.setEditable(false);
        editPanel.add(name);

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setBounds(20, 60, 150, 35);
        codeLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(codeLabel);

        code = new JTextField();
        code.setBounds(200, 60, 250, 35);
        code.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        code.setEditable(false);
        editPanel.add(code);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 100, 150, 35);
        quantityLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(quantityLabel);

        quantity = new JTextField();
        quantity.setBounds(200, 100, 250, 35);
        quantity.addKeyListener(this);
        quantity.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        quantity.setEditable(false);
        editPanel.add(quantity);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 140, 150, 35);
        priceLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(priceLabel);

        price = new JTextField();
        price.setBounds(200, 140, 250, 35);
        price.setVisible(true);
        price.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        price.setEditable(false);
        editPanel.add(price);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, 180, 150, 35);
        descLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(descLabel);

        description = new JTextField();
        description.setBounds(200, 180, 250, 35);
        description.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        description.setEditable(false);
        editPanel.add(description);

        //inventory warning settings
        JLabel alertValueLabel = new JLabel("Alert Limit:");
        alertValueLabel.setBounds(20, 220, 150, 35);
        alertValueLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(alertValueLabel);

        alertValue = new JTextField();
        alertValue.setText("0");
        alertValue.setBounds(200, 220, 50, 35);
        alertValue.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        alertValue.setEditable(false);
        editPanel.add(alertValue);

        alertFlag = new JCheckBox("ON/OFF");
        alertFlag.setBounds(300, 220, 150, 25);
        alertFlag.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(alertFlag);
        alertFlag.setSelected(true);

        //label printing
        JLabel printCountLabel = new JLabel("# of labels to print:");
        printCountLabel.setBounds(20, 300, 200, 35);
        printCountLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(printCountLabel);

        printCount = new JTextField();
        printCount.setText("0");
        printCount.setBounds(220, 300, 70, 35);
        printCount.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(printCount);

        print = new JButton("Print");
        print.setBounds(300, 300, 150, 35);
        print.addActionListener(this);
        print.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(print);

        //external bare code
        JLabel barcodeLabel = new JLabel("Barcode:");
        barcodeLabel.setBounds(20, 260, 200, 25);
        barcodeLabel.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(barcodeLabel);


        bareCodeValue = new JTextField();
        bareCodeValue.setBounds(200, 260, 250, 35);
        bareCodeValue.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 20));
        editPanel.add(bareCodeValue);
        bareCodeValue.setEditable(false);

        if (product != null) {
            name.setText(product.getName());
            code.setText(product.getCode());
            price.setText("" + product.getPrice());
            quantity.setText("" + product.getQuantity());
            description.setText(product.getDescription());
            printCount.setText(Integer.toString(0));
            alertValue.setText(product.getAlertValue());
            alertFlag.setSelected(product.isAlertFlag());
            bareCodeValue.setText(product.getBareCode());
            bareCodeValue.setVisible(true);
            editImage(product);

        } else {
            name.setText("");
            code.setText("");
            price.setText("");
            quantity.setText("");
            description.setText("");
            printCount.setText("0");
            alertValue.setText("0");

            price.setEnabled(true);
        }
        this.add(editPanel);
        editPanel.validate();
        editPanel.repaint();
        this.validate();
        this.repaint();
    }

    public void editImage(Product product) {

        if(imagePanel != null) {
            this.remove(imagePanel);
        }
        Image image = Toolkit.getDefaultToolkit().createImage(product.getImage());
        int x = 550;
        int y = 480;
        imagePanel = new ImagePanel(image, x, y);
        imagePanel.setBounds(600, 100, x, y);
        this.add(imagePanel);

        this.repaint();
        this.validate();

    }


    public void actionPerformed(ActionEvent e) {

        bcSearch.requestFocusInWindow();

        //label printing script
        ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        if (e.getSource() instanceof JButton && e.getSource() == print) {
            if (true) {
                Product product = null;
                try {
                    product = service.getProductByBarCode(bareCodeValue.getText());
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                int count = 0;
                try {
                    count = Integer.parseInt(printCount.getText());
                } catch (NumberFormatException nfe) {
                    logger.error("Error parsing label count: " + nfe.getMessage());
                    JOptionPane.showMessageDialog(null, "Invalid for print label number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
                PrintUtil.printLabel(product, count);
            } else {
                JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        //radio buttons script
        if (e.getSource() instanceof JRadioButton) {
            if (e.getSource().equals(code8)) {
                barcodeLength = 8;
            } else if (e.getSource().equals(code12)) {
                barcodeLength = 12;
            } else if (e.getSource().equals(code13)) {
                barcodeLength = 13;
            } else if (e.getSource().equals(addOperation)) {
                quantityAddition = 1;
            } else if (e.getSource().equals(removeOperation)) {
                quantityAddition = -1;
            }
        }

        ImageButton trigger = (ImageButton) e.getSource();
        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }

        this.repaint();
        this.validate();
    }

    public void keyReleased(KeyEvent e) {

        if (e.getSource().equals(bcSearch)) {
            String barcode = bcSearch.getText();
            if (barcode.length() == barcodeLength) {
                ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
                boolean result = false;
                try {
                    result = service.updateProductQuantityByBarCode(barcode, quantityAddition);
                    if (result) {
                        Product product = service.getProductByBarCode(barcode);
                        ProductUpdate productUpdate = new ProductUpdate();
                        productUpdate.setProductId(product.getId());
                        productUpdate.setAddDate(new Date());
                        productUpdate.setOperation(UPDATE);
                        productUpdate.setQuantity(quantityAddition);
                        service.storeProductUpdate(productUpdate);
                        edit(product);
                    } else {
                        Message.show("Error", "Product not found", 0);
                    }
                } catch (ServiceException e1) {
                    Message.show(e1);
                } finally {
                    bcSearch.setText("");
                    bcSearch.requestFocus();
                }
            }
        }
        this.repaint();
        this.validate();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
    }
}
