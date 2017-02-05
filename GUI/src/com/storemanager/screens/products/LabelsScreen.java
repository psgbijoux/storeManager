package com.storemanager.screens.products;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Product;
import com.storemanager.models.Role;
import com.storemanager.service.ProductService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class LabelsScreen extends AbstractPanel {
    private Window baseWindow;
    private JPanel searchPanel;

    private JLabel priceLabel, weightLabel, barcodeLabel;
    private JComboBox<Role> priceQtySelect, weightQtySelect, barcodeQtySelect;
    private JTextField price, weight, weightLabel1, weightLabel2, barcode;
    private JButton pricePrint, weightPrint, barcodePrint, close;

    private final Font defaultFont = new Font("Lucida Grande", Font.PLAIN, 20);
    private static final ArrayList<Integer> quantities = new ArrayList<>();

    public LabelsScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(800, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(670, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        setQuantities();
        createLabelCriteria();

        this.repaint();
        this.validate();
    }

    private void createLabelCriteria() {
        searchPanel = new JPanel();
        searchPanel.setBounds(30, 30, 740, 390);
        searchPanel.setBackground(Color.lightGray);
        searchPanel.setLayout(null);
        this.add(searchPanel);

        JLabel criteriaType = new JLabel("Administrare Etichete");
        criteriaType.setBounds(25, 10, 300, 50);
        criteriaType.setFont(defaultFont);
        searchPanel.add(criteriaType);

        //***************
        barcodeLabel = new JLabel("Eticheta cod bare:");
        barcodeLabel.setBounds(25, 50, 180, 50);
        barcodeLabel.setFont(defaultFont);
        searchPanel.add(barcodeLabel);

        barcodeQtySelect = new JComboBox(quantities.toArray());
        barcodeQtySelect.setBounds(200, 50, 90, 50);
        barcodeQtySelect.setFont(defaultFont);
        barcodeQtySelect.addActionListener(this);
        searchPanel.add(barcodeQtySelect);

        barcode = new JTextField();
        barcode.setBounds(300, 60, 230, 30);
        barcode.setFont(defaultFont);
        searchPanel.add(barcode);

        barcodePrint = new JButton("Print");
        barcodePrint.setBounds(550, 57, 100, 35);
        barcodePrint.setFont(defaultFont);
        barcodePrint.addActionListener(this);
        searchPanel.add(barcodePrint);

        //*************************************
        priceLabel = new JLabel("Eticheta pret:");
        priceLabel.setBounds(25, 90, 150, 50);
        priceLabel.setFont(defaultFont);
        searchPanel.add(priceLabel);

        priceQtySelect = new JComboBox(quantities.toArray());
        priceQtySelect.setBounds(200, 90, 90, 50);
        priceQtySelect.setFont(defaultFont);
        priceQtySelect.addActionListener(this);
        searchPanel.add(priceQtySelect);

        price = new JTextField();
        price.setBounds(300, 100, 230, 30);
        price.setFont(defaultFont);
        searchPanel.add(price);

        pricePrint = new JButton("Print");
        pricePrint.setBounds(550, 98, 100, 35);
        pricePrint.setFont(defaultFont);
        pricePrint.addActionListener(this);
        searchPanel.add(pricePrint);

        //***************
        weightLabel = new JLabel("Eticheta gramaj:");
        weightLabel.setBounds(25, 130, 180, 50);
        weightLabel.setFont(defaultFont);
        searchPanel.add(weightLabel);

        weightQtySelect = new JComboBox(quantities.toArray());
        weightQtySelect.setBounds(200, 130, 90, 50);
        weightQtySelect.setFont(defaultFont);
        weightQtySelect.addActionListener(this);
        searchPanel.add(weightQtySelect);

        weight = new JTextField();
        weight.setBounds(300, 140, 230, 30);
        weight.setFont(defaultFont);
        searchPanel.add(weight);

        weightLabel1 = new JTextField();
        weightLabel1.setBounds(300, 170, 120, 25);
        searchPanel.add(weightLabel1);

        weightLabel2 = new JTextField();
        weightLabel2.setBounds(420, 170, 110, 25);
        searchPanel.add(weightLabel2);

        weightPrint = new JButton("Print");
        weightPrint.setBounds(550, 138, 100, 35);
        weightPrint.setFont(defaultFont);
        weightPrint.addActionListener(this);
        searchPanel.add(weightPrint);

//        hideAllCriteria();
        this.repaint();
        this.validate();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(pricePrint)) {
            PrintUtil.printPriceLabel(price.getText(), priceQtySelect.getSelectedIndex() + 1);
            return;
        }
        if (e.getSource().equals(weightPrint)) {
            PrintUtil.printWeightLabel(weight.getText(), weightLabel1.getText(), weightLabel2.getText(), weightQtySelect.getSelectedIndex() + 1);
            return;
        }
        if (e.getSource().equals(barcodePrint)) {
            ProductService productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
            try {
                Product product = productService.getProductByBarCode(barcode.getText());
                if (product != null) {
                    PrintUtil.printLabel(product, barcodeQtySelect.getSelectedIndex() + 1);
                }
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
            return;
        }
        ImageButton trigger = (ImageButton) e.getSource();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
    }

    private static void setQuantities() {
        for (int i=1; i <= 100; i++) {
            quantities.add(i);
        }
    }
}
