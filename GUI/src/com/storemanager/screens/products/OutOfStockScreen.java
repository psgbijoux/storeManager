package com.storemanager.screens.products;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Category;
import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.models.Role;
import com.storemanager.screens.sales.ImagePanel;
import com.storemanager.service.CategoryService;
import com.storemanager.service.ProductService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

public class OutOfStockScreen extends AbstractPanel implements ItemListener {
    private StoreLogger logger = StoreLogger.getInstance(ProductScreen.class);
    private Window baseWindow;
    private JButton close, preview, edit, cancel, save, delete, print;
    private JPanel editPanel;
    private JTextField name, code, price, quantity, description, weight, printCount, alertValue, bareCodeValue, bcSearch;
    private JCheckBox alertFlag, bareCodeFlag;
    private JComboBox<Role> categoryBox;
    private JScrollPane scrollPane;
    private JTable table;
    private String command;
    private JRadioButton gold, other;
    private Product editedProduct;

    private final static String UPDATE = "UPDATE";

    public OutOfStockScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(900, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(760, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        //action buttons
        preview = new ImageButton(ButtonEnum.PREVIEW, this);
        preview.setLocation(510, 30);
        preview.setSize(ButtonSizeEnum.LONG.getSize());
        this.add(preview);

        edit = new ImageButton(ButtonEnum.EDIT, this);
        edit.setLocation(640, 30);
        edit.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(edit);

        delete = new ImageButton(ButtonEnum.DELETE, this);
        delete.setLocation(760, 30);
        delete.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(delete);

        //role ddl
        JLabel roleLabel = new JLabel("Category:");
        roleLabel.setBounds(30, 30, 100, 30);
        this.add(roleLabel);

        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
        java.util.List<Category> categoryList = null;
        try {
            categoryList = service.getAllCategories();
        } catch (ServiceException e) {
            Message.show(e);
        }
        categoryBox = new JComboBox(categoryList.toArray());
        categoryBox.setBounds(90, 30, 340, 25);
        categoryBox.addActionListener(this);
        this.add(categoryBox);

        Category category = (Category) categoryBox.getSelectedItem();
        loadProducts(category.getId());

        //search by bare code
        JLabel bcLabel = new JLabel("Bare Code:");
        bcLabel.setLocation(510, 90);
        bcLabel.setSize(100, 25);
        this.add(bcLabel);

        bcSearch = new JTextField();
        bcSearch.setLocation(580, 90);
        bcSearch.setSize(200, 25);
        bcSearch.addKeyListener(this);
        this.add(bcSearch);
        bcSearch.requestFocus();
    }

    private void loadProducts(int categoryId) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Code");
        tableModel.addColumn("Bare Code");
        tableModel.addColumn("Price");
        tableModel.addColumn("Quantity");
        ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        java.util.List<Product> products = new ArrayList<Product>();
        try {
            products = service.getOutOfStockProductsByCategoryId(categoryId);
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (Product product : products) {
            Object[] data = new Object[6];
            data[0] = product.getId();
            data[1] = product.getName();
            data[2] = product.getCode();
            data[3] = product.getBareCode();
            data[4] = product.getPrice();
            data[5] = product.getQuantity();
            tableModel.addRow(data);
        }
        table = new JTable(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(147);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(40);
        table.getColumnModel().getColumn(5).setPreferredWidth(40);

        if (scrollPane != null) {
            this.remove(scrollPane);
        }
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    preview.doClick();
                }
            }
        });
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 70, 420, 500);
        this.add(scrollPane);
        this.repaint();
    }

    private void edit(Product product) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(510, 140, 360, 370);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setBounds(100, 20, 200, 25);
        editPanel.add(name);

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setBounds(20, 50, 100, 25);
        editPanel.add(codeLabel);

        code = new JTextField();
        code.setBounds(100, 50, 200, 25);
        editPanel.add(code);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 80, 100, 25);
        editPanel.add(quantityLabel);

        quantity = new JTextField();
        quantity.setBounds(100, 80, 200, 25);
        quantity.addKeyListener(this);
        editPanel.add(quantity);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 110, 100, 25);
        editPanel.add(priceLabel);

        price = new JTextField();
        price.setBounds(100, 110, 200, 25);
        editPanel.add(price);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, 140, 100, 25);
        editPanel.add(descLabel);

        description = new JTextField();
        description.setBounds(100, 140, 200, 25);
        editPanel.add(description);

        JLabel productTypeLabel = new JLabel("Product type:");
        productTypeLabel.setBounds(20, 170, 100, 25);
        editPanel.add(productTypeLabel);

        ButtonGroup buttonGroup = new ButtonGroup();
        gold = new JRadioButton("GOLD");
        gold.setBounds(100, 170, 80, 25);
        gold.addActionListener(this);
        buttonGroup.add(gold);
        other = new JRadioButton("OTHER");
        other.setBounds(180, 170, 80, 25);
        other.addActionListener(this);
        gold.setEnabled(false);
        other.setEnabled(false);
        buttonGroup.add(other);

        editPanel.add(gold);
        editPanel.add(other);

        JLabel weightLabel = new JLabel("Weight:");
        weightLabel.setBounds(20, 200, 100, 25);
        editPanel.add(weightLabel);

        weight = new JTextField();
        weight.setBounds(100, 200, 110, 25);
        editPanel.add(weight);

        //inventory warning settings
        JLabel alertValueLabel = new JLabel("Alert Limit:");
        alertValueLabel.setBounds(20, 230, 100, 25);
        editPanel.add(alertValueLabel);

        alertValue = new JTextField();
        alertValue.setText("0");
        alertValue.setBounds(100, 230, 110, 25);
        editPanel.add(alertValue);

        alertFlag = new JCheckBox("ON/OFF");
        alertFlag.setBounds(220, 230, 75, 25);
        editPanel.add(alertFlag);
        alertFlag.setSelected(true);

        //label printing
        JLabel printCountLabel = new JLabel("# of labels to print:");
        printCountLabel.setBounds(20, 290, 140, 25);
        editPanel.add(printCountLabel);

        printCount = new JTextField();
        printCount.setText("0");
        printCount.setBounds(140, 290, 70, 25);
        editPanel.add(printCount);

        print = new JButton("Print");
        print.setBounds(215, 290, 80, 25);
        print.addActionListener(this);
        editPanel.add(print);

        //external bare code
        bareCodeValue = new JTextField();
        bareCodeValue.setBounds(175, 260, 120, 25);
        editPanel.add(bareCodeValue);
        bareCodeValue.setEditable(false);

        bareCodeFlag = new JCheckBox("Generate Bare Code");
        bareCodeFlag.setBounds(20, 260, 145, 25);
        bareCodeFlag.addItemListener(this);
        editPanel.add(bareCodeFlag);
        bareCodeFlag.setSelected(true);

        //action buttons
        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(210, 320);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(40, 320);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (product != null) {
            name.setText(product.getName());
            code.setText(product.getCode());
            price.setText("" + product.getPrice());
            quantity.setText("" + product.getQuantity());
            description.setText(product.getDescription());
            printCount.setText(Integer.toString(0));
            if (product.isGold()) {
                gold.setSelected(true);
                price.setEnabled(false);
                weight.setEnabled(true);
            } else if (product.isOther()) {
                other.setSelected(true);
                price.setEnabled(true);
                weight.setEnabled(false);
            }
            weight.setText(Double.toString(product.getWeight()));
            alertValue.setText(product.getAlertValue());
            alertFlag.setSelected(product.isAlertFlag());
            bareCodeFlag.setSelected(product.isGenerateBareCodeFlag());
            bareCodeFlag.setEnabled(false);
            bareCodeValue.setText(product.getBareCode());
            bareCodeValue.setVisible(true);
        } else {
            name.setText("");
            code.setText("");
            price.setText("");
            quantity.setText("");
            description.setText("");
            printCount.setText("0");
            weight.setText("");
            alertValue.setText("0");

            other.setSelected(true);
            price.setEnabled(true);
            weight.setEnabled(false);
        }
        this.add(editPanel);
        this.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox && e.getSource() == categoryBox) {
            //implement change listener
            Category category = (Category) categoryBox.getSelectedItem();
            loadProducts(category.getId());
            return;
        }
        ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        if (e.getSource() instanceof JButton && e.getSource() == print) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int id = Integer.parseInt(table.getValueAt(index, 0).toString());
                Product product = null;
                try {
                    product = service.load(id);
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
            return;
        }
        if (e.getSource() instanceof JRadioButton) {
            if (e.getSource().equals(gold)) {
                price.setEnabled(false);
                weight.setEnabled(true);
                quantity.setEnabled(false);
                quantity.setText("1");
                weight.setText("");
            } else if (e.getSource().equals(other)) {
                price.setEnabled(true);
                weight.setEnabled(false);
                quantity.setEnabled(true);
                quantity.setText("");
                weight.setText("0.0");

            }
            return;
        }

        ImageButton trigger = (ImageButton) e.getSource();

        Category category = (Category) categoryBox.getSelectedItem();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.PREVIEW.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int id = Integer.parseInt(table.getValueAt(index, 0).toString());
                Product product = null;
                try {
                    product = service.load(id);
                    Image image = Toolkit.getDefaultToolkit().createImage(product.getImage());
                    int x = 640;
                    int y = 480;
                    JPanel imagePanel = new ImagePanel(image, x, y);
                    imagePanel.setBounds(0, 0, x, y);
                    JDialog popup = new JDialog();
                    popup.setResizable(false);
                    popup.setBackground(Color.LIGHT_GRAY);
                    popup.setAlwaysOnTop(true);
                    popup.setSize(x + 5, y + 30);
                    popup.setLayout(null);
                    popup.setLocationRelativeTo(null);
                    popup.add(imagePanel);
                    popup.setVisible(true);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete product?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int productId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        service.delete(new Product(productId));
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    loadProducts(category.getId());
                    JOptionPane.showMessageDialog(null, "Product deleted.");
                    if (editPanel != null)
                        this.remove(editPanel);
                    this.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int id = Integer.parseInt(table.getValueAt(index, 0).toString());
                Product product = null;
                try {
                    product = service.load(id);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                command = "EDIT";
                edit(product);
                editedProduct = product.clone();
            } else {
                JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            bcSearch.setText("");
            bcSearch.requestFocus();
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Name", name.getText()));
            sb.append(FieldValidator.validateStringField("Code", code.getText()));
            if (other.isSelected()) {
                sb.append(FieldValidator.validateIntField("Quantity", quantity.getText()));
                sb.append(FieldValidator.validateDoubleField("Price", price.getText()));
            } else {
                sb.append(FieldValidator.validateDoubleField("Weight", weight.getText()));
            }
            sb.append(FieldValidator.validateIntField("Alert Limit", alertValue.getText()));
            if (!bareCodeFlag.isSelected()) {
                sb.append(FieldValidator.validateStringField("Bare Code", bareCodeValue.getText()));
            } else {
                sb.append(FieldValidator.validateIntField("# of labels", printCount.getText()));
            }
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("EDIT".equals(command)) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int id = Integer.parseInt(table.getValueAt(index, 0).toString());
                    Product product = null;
                    try {
                        product = service.load(id);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    product.setName(name.getText());
                    product.setDescription(description.getText());
                    product.setCode(code.getText());
                    product.setGold(gold.isSelected());
                    product.setOther(other.isSelected());
                    product.setAlertFlag(alertFlag.isSelected());
                    product.setAlertValue(alertValue.getText());

                    if (other.isSelected()) {
                        product.setQuantity(Integer.parseInt(quantity.getText()));
                        product.setWeight(0.0);
                        BigDecimal priceValue = new BigDecimal(price.getText());
                        priceValue = priceValue.setScale(2, RoundingMode.HALF_UP);
                        product.setPrice(priceValue);
                    } else {
                        product.setPrice(new BigDecimal(0));
                        BigDecimal weightValue = new BigDecimal(weight.getText());
                        weightValue = weightValue.setScale(2, RoundingMode.HALF_UP);
                        product.setWeight(weightValue.doubleValue());
                        product.setQuantity(1);
                    }

                    if (!bareCodeFlag.isSelected()) {
                        product.setBareCode(bareCodeValue.getText());
                    }
                    try {
                        service.update(product, null);
                    } catch (ServiceException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    ProductUpdate iu = new ProductUpdate();
                    iu.setAddDate(new Date());
                    iu.setOperation(UPDATE);
                    if (other.isSelected()) {
                        iu.setQuantity(product.getQuantity() - editedProduct.getQuantity());
                        iu.setWeight(0.0);
                    } else {
                        iu.setWeight(product.getWeight() - editedProduct.getWeight());
                        iu.setQuantity(0);
                    }
                }
            }
            loadProducts(category.getId());
            bcSearch.setText("");
            bcSearch.requestFocus();
            this.repaint();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getSource().equals(quantity)) {
            if (bareCodeFlag.isSelected() && command != "EDIT") {
                double qty = Double.parseDouble(quantity.getText());
                printCount.setText(Integer.toString((int) qty));
            }
        }
        if (e.getSource().equals(bcSearch)) {
            String bareCode = bcSearch.getText();
            if (bareCode.length() == 8 || bareCode.length() > 12) {
                ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
                try {
                    Product product = service.getProductByCode(bareCode);
                    edit(product);
                    quantity.requestFocus();
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
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
