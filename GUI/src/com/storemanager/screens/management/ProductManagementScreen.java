package com.storemanager.screens.management;

import com.storemanager.components.*;
import com.storemanager.components.Window;
import com.storemanager.models.*;
import com.storemanager.service.*;
import com.storemanager.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementScreen extends AbstractManagementScreen {

    private JButton add, edit, delete, search;
    private JTextField bcSearch;

    private ProductService productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);

    public ProductManagementScreen(Window baseWindow) {

        super(baseWindow);

        add = new JButton("add");
        add.setBounds(500, 5, 80, 20);
        add.addActionListener(this);
        this.add(add);

        edit = new JButton("edit");
        edit.setBounds(585, 5, 80, 20);
        edit.addActionListener(this);
        this.add(edit);

        delete = new JButton("delete");
        delete.setBounds(670, 5, 80, 20);
        delete.addActionListener(this);
        this.add(delete);

        JLabel barcodeLabel = new JLabel("Barcode:");
        barcodeLabel.setBounds(500, 655, 100, 25);
        this.add(barcodeLabel);

        bcSearch = new JTextField();
        bcSearch.setLocation(500, 680);
        bcSearch.setSize(400, 40);
        bcSearch.setFont(new Font(bcSearch.getFont().getName(), Font.PLAIN, 30));
        this.add(bcSearch);

        search = new ImageButton(ButtonEnum.BARCODEHISTORY, this);
        search.setLocation(900, 680);
        search.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(search);
    }

    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if (e.getSource() instanceof JButton) {
            if (e.getSource() == add) {
                super.command = "ADD";
                if (editPanel != null) {
                    this.remove(editPanel);
                }
                if (supplyLabel != null) {
                    this.remove(supplyLabel);
                    this.remove(salesLabel);
                    this.remove(supplyScrollPane);
                    this.remove(salesScrollPane);
                }
                if (super.editedProduct != null) {
                    super.editedProduct = null;
                }
                this.repaint();
                loadProductForm(null);
            } else if (e.getSource() == edit) {
                if (editPanel != null)
                    this.remove(editPanel);
                int index = table.getSelectedRow();
                if (index >= 0) {
                    String[] idArray = (String[]) table.getValueAt(index, 0);
                    int id = Integer.parseInt(idArray[0]);
                    Product product;
                    try {
                        product = productService.load(id);
                        super.command = "EDIT";
                        loadProductForm(product);
                        loadSaleHistory(product);
                        loadSupplyHistory(product);
                        super.editedProduct = product.clone();
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == delete) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int response = JOptionPane.showConfirmDialog(null, "Do you want to delete product?", "Delete", JOptionPane.YES_NO_OPTION);
                    if (response == 0) {
                        String[] idArray = (String[]) table.getValueAt(index, 0);
                        int id = Integer.parseInt(idArray[0]);
                        try {
                            productService.delete(new Product(id));

                            lastPage = getLastPageNo();
                            loadSearchData(currentPage, null);
                        } catch (ServiceException e1) {
                            Message.show(e1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "Product deleted.");
                        if (editPanel != null) {
                            this.remove(editPanel);
                        }
                        if (supplyLabel !=  null) {
                            this.remove(supplyLabel);
                            this.remove(salesLabel);
                            this.remove(supplyScrollPane);
                            this.remove(salesScrollPane);
                        }
                        this.repaint();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (e.getSource() instanceof ImageButton) {
            ImageButton trigger = (ImageButton) e.getSource();
            if (trigger.getCommand().equals(ButtonEnum.BARCODEHISTORY.getCommand())){
                if (editPanel != null) {
                    this.remove(editPanel);
                }
                this.repaint();
                try {
                    String barcodeStr = bcSearch.getText();
                    if (barcodeStr == "") {
                        JOptionPane.showMessageDialog(null, "Enter Barcode!");
                    } else {
                        Product product = productService.getProductByCode(barcodeStr);
                        loadProductForm(product);
                        loadSupplyHistory(product);
                        loadSaleHistory(product);
                        loadSearchData(0, product);
                        super.command = "SEARCH";
                        bcSearch.setText("");
                    }
                } catch (ServiceException e1) {
                    JOptionPane.showMessageDialog(null, "Products not found!");
                } catch (IOException e2) {
                    JOptionPane.showMessageDialog(null, "Product not found!");
                }

            } else if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
                String validationMessage = validateProductForm();
                if (!validationMessage.isEmpty()) {
                    JOptionPane.showMessageDialog(null, validationMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ("ADD".equals(super.command)) {
                    try {
                        Product product = new Product();
                        product.setCategoryId(currentCategory.getId());
                        product.setGenerateBareCodeFlag(bareCodeFlag.isSelected());
                        updateProductByProductForm(product);
                        updateProductBarCode(product);
                        BufferedImage image = updateImageByProductForm();
                        productService.add(product, image);

                        ProductUpdate productUpdate = computeUpdateByProductForm(product, null, ADD);
                        productUpdate.setProductId(product.getId());
                        productService.storeProductUpdate(productUpdate);

                        lastUsedProduct = product;
                        JOptionPane.showMessageDialog(null, "Product added with success!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        lastPage = getLastPageNo();
                        loadSearchData(currentPage, null);

                        int count = Integer.parseInt(printCount.getText());
                        PrintUtil.printLabel(product, count);
                    } catch (ServiceException se) {
                        JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("SEARCH".equals(super.command)) {
                    try {
                        Product product = productService.getProductByCode(editedProduct.getBareCode());
                        updateProductByProductForm(product);
                        BufferedImage image = updateImageByProductForm();
                        productService.update(product, image);

                        ProductUpdate productUpdate = computeUpdateByProductForm(product, editedProduct, UPDATE);
                        productUpdate.setProductId(product.getId());
                        productService.storeProductUpdate(productUpdate);

                        lastUsedProduct = product;
                    } catch (ServiceException se) {
                        JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            this.repaint();
        }
    }

    @Override
    protected List<Product> getPaginatedData(int currentPage, Product product) {
        List<Product> products = new ArrayList<>();
        try {
            if (product != null) {
                products.add(product);
            } else {
                products.addAll(productService.getProductsByCategoryId(currentCategory.getId(), currentPage));
            }
        } catch (ServiceException e) {
            Message.show(e);
        }
        return products;
    }

    @Override
    protected int getLastPageNo() {
        int lastPage = productService.countProductsByCategoryId(currentCategory.getId());
        return lastPage;
    }

    @Override
    protected Object[] generateTableRowData(Object object) {
        Product product = (Product) object;
        DateFormat df = new SimpleDateFormat("dd/MM/yy");

        String[] id = new String[1];
        id[0] = String.valueOf(product.getId());

        String[] info = new String[5];
        info[0] = product.getName() + " - " + product.getCode();
        info[1] = product.isGold() ? product.getDescription() + " | weight: " + product.getWeight() : product.getDescription();
        info[2] = "cod bare: " + product.getBareCode();
        info[3] = "price: " + String.valueOf(product.getPrice());
        info[4] = "stock: " + String.valueOf(product.getQuantity());

        Object[] data = new Object[3];
        data[0] = id;
        data[1] = info;
        data[2] = generateTableImageIcon(product);
        return data;
    }

    private void updateProductBarCode(Product product) {
        if (bareCodeFlag.isSelected()) {
            String bareCodeVal = null;
            try {
                bareCodeVal = BareCodeGenerator.generateCode8Digit(currentCategory.getId(), productService.getBareCodeCount());
            } catch (ServiceException e1) {
                Message.show(e1);
            }
            product.setBareCode(bareCodeVal);
        } else {
            product.setBareCode(bareCodeValue.getText());
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getSource().equals(quantity)) {
            if (bareCodeFlag.isSelected() && command != "EDIT") {
                double qty = Double.parseDouble(quantity.getText());
                printCount.setText(Integer.toString((int) qty));
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(bareCodeFlag)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                bareCodeValue.setVisible(false);
                bareCodeValue.setText("");
            } else {
                bareCodeValue.setVisible(true);
            }
        }
    }
}
