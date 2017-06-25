package com.storemanager.screens.products;

import com.storemanager.components.*;
import com.storemanager.components.Window;
import com.storemanager.models.*;
import com.storemanager.renderers.MultiLineTableCellRenderer;
import com.storemanager.screens.sales.ImagePanel;
import com.storemanager.service.*;
import com.storemanager.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductManagement extends AbstractPanel implements ItemListener{

    private StoreLogger logger = StoreLogger.getInstance(ProductManagement.class);

    private static final int FIRST_PAGE = 1;

    private ProductService productService;

    private JButton add, edit, delete, search;
    private JButton next, prev, first, last;
    private JComboBox<Role> categoryBox;
    private JTextField bcSearch;
    private JTable table, supplyTable, salesTable;
    private JLabel supplyLabel, salesLabel;
    private JScrollPane supplyScrollPane, salesScrollPane;

    private JButton browse, print, cancel, save;
    private JCheckBox alertFlag, bareCodeFlag;
    private JRadioButton gold, other;
    private JTextField name, code, price, quantity, description, weight;
    private JTextField imagePath, printCount, alertValue, bareCodeValue;
    private JPanel editPanel;

    private Window baseWindow;

    private final static String ADD = "ADD";
    private final static String UPDATE = "UPDATE";

    private Category currentCategory;
    private Product lastUsedProduct, editedProduct;

    private String command;

    private int currentPage;
    private int lastPage;

    public ProductManagement(Window baseWindow) {

        productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);

        this.baseWindow = baseWindow;
        this.setSize(1200, 750);
        this.setBackground(Color.gray);
        this.setLayout(null);

        //role ddl
        JLabel roleLabel = new JLabel("Category:");
        roleLabel.setBounds(10, 3, 100, 30);
        this.add(roleLabel);

        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
        List<Category> categoryList = null;
        try {
            categoryList = service.getAllCategories();
        } catch (ServiceException e) {
            Message.show(e);
        }
        categoryBox = new JComboBox(categoryList.toArray());
        categoryBox.setBounds(70, 5, 200, 25);
        categoryBox.addActionListener(this);
        this.add(categoryBox);

        first = new JButton("<<");
        first.setBounds(295, 5, 40, 20);
        first.addActionListener(this);
        this.add(first);

        prev = new JButton("<");
        prev.setBounds(340, 5, 40, 20);
        prev.addActionListener(this);
        this.add(prev);

        next = new JButton(">");
        next.setBounds(385, 5, 40, 20);
        next.addActionListener(this);
        this.add(next);

        last = new JButton(">>");
        last.setBounds(430, 5, 40, 20);
        last.addActionListener(this);
        this.add(last);

        add = new JButton("add");
        add.setBounds(500, 5, 60, 20);
        add.addActionListener(this);
        this.add(add);

        edit = new JButton("edit");
        edit.setBounds(565, 5, 60, 20);
        edit.addActionListener(this);
        this.add(edit);

        delete = new JButton("delete");
        delete.setBounds(630, 5, 60, 20);
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

    private void loadProducts(int categoryId, int currentPage) throws IOException {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Photo");
        tableModel.addColumn("Name");
        List<Product> products = new ArrayList<>();
        try {
            products = productService.getProductsByCategoryId(categoryId, currentPage);
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (Product product : products) {
            Object[] data = new Object[3];

            InputStream in = new ByteArrayInputStream(product.getImage());
            BufferedImage bi = ImageIO.read(in);

            BufferedImage resizedImage = ImageUtil.resizeImage(bi, BufferedImage.TYPE_INT_ARGB, 200, 200);

            ImageIcon imageIcon2 = new ImageIcon(resizedImage);

            String[] id = new String[1];
            id[0] = String.valueOf(product.getId());

            String[] info = new String[5];
            info[0] = product.getName() + " - " + product.getCode();
            info[1] = product.getDescription();
            info[2] = "cod bare: " + product.getBareCode();
            info[3] = "pret: " + String.valueOf(product.getPrice());
            info[4] = "cantitate: " + String.valueOf(product.getQuantity());

            data[0] = id;
            data[1] = info;
            data[2] = imageIcon2;
            tableModel.addRow(data);
        }

        if (table != null) {
            this.remove(table);
        }

        table = new JTable(tableModel) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

        MultiLineTableCellRenderer renderer = new MultiLineTableCellRenderer();

        table.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        table.setRowHeight(86);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setWidth(50);
        table.getColumnModel().getColumn(1).setWidth(200);
        table.getColumnModel().getColumn(2).setWidth(200);
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = table.getSelectedRow();
                    if (index >= 0) {
                        String[] idArray = (String[]) table.getValueAt(index, 0);
                        int id = Integer.parseInt(idArray[0]);
                        Product product = null;
                        try {
                            product = productService.load(id);
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
            }
        });

        table.setBounds(20, 33, 450, 687);
        this.add(table);
        this.repaint();
    }

    private void loadSupplyHistory(Product product) {

        supplyLabel = new JLabel("Supplies:");
        supplyLabel.setBounds(500, 460, 100, 25);
        this.add(supplyLabel);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Supply Date");
        tableModel.addColumn("Quantity");
        final ProductService service = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        List<ProductUpdate> productSupplyData = new ArrayList<ProductUpdate>();
        try {
            productSupplyData = service.getProductUpdateData(product);
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (ProductUpdate productUpdate: productSupplyData) {
            Object[] data = new Object[3];
            data[0] = productUpdate.getId();
            data[1] = productUpdate.getAddDate();
            data[2] = productUpdate.getQuantity();
            tableModel.addRow(data);
        }
        supplyTable = new JTable(tableModel);

        supplyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        supplyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        supplyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        supplyTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        if (supplyScrollPane != null) {
            this.remove(supplyScrollPane);
        }

        supplyScrollPane = new JScrollPane(supplyTable);
        supplyScrollPane.setBounds(500, 485, 300, 150);
        this.add(supplyScrollPane);
        this.repaint();

    }

    private void loadSaleHistory(Product product) {

        salesLabel = new JLabel("Sales:");
        salesLabel.setBounds(820, 460, 100, 25);
        this.add(salesLabel);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Sale Date");
        tableModel.addColumn("Quantity");
        final SaleService service = ServiceLocator.getService(ServiceName.SALE_SERVICE);
        List<SaleDetail> productSaleData = new ArrayList<SaleDetail>();
        try {
            productSaleData = service.getSaleData(product);
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (SaleDetail saleDetail: productSaleData) {
            Object[] data = new Object[3];
            data[0] = saleDetail.getId();
            data[1] = saleDetail.getAddDate();
            data[2] = saleDetail.getQuantity();
            tableModel.addRow(data);
        }
        salesTable = new JTable(tableModel);

        salesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        if (salesScrollPane != null) {
            this.remove(salesScrollPane);
        }

        salesScrollPane = new JScrollPane(salesTable);
        salesScrollPane.setBounds(820, 485, 300, 150);
        this.add(salesScrollPane);
    }

    private void edit(Product product) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(500, 33, 360, 410);
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

        JLabel imageLabel = new JLabel("Image:");
        imageLabel.setBounds(20, 170, 100, 25);
        editPanel.add(imageLabel);

        imagePath = new JTextField();
        imagePath.setBounds(100, 170, 160, 25);
        editPanel.add(imagePath);

        browse = new JButton("Browse");
        browse.setBounds(265, 170, 80, 25);
        browse.addActionListener(this);
        editPanel.add(browse);

        JLabel productTypeLabel = new JLabel("Product type:");
        productTypeLabel.setBounds(20, 200, 100, 25);
        editPanel.add(productTypeLabel);

        ButtonGroup buttonGroup = new ButtonGroup();
        gold = new JRadioButton("GOLD");
        gold.setBounds(100, 200, 80, 25);
        gold.addActionListener(this);
        buttonGroup.add(gold);
        other = new JRadioButton("OTHER");
        other.setBounds(180, 200, 80, 25);
        other.addActionListener(this);
        buttonGroup.add(other);

        editPanel.add(gold);
        editPanel.add(other);

        JLabel weightLabel = new JLabel("Weight:");
        weightLabel.setBounds(20, 230, 100, 25);
        editPanel.add(weightLabel);

        weight = new JTextField();
        weight.setBounds(100, 230, 110, 25);
        editPanel.add(weight);

        //inventory warning settings
        JLabel alertValueLabel = new JLabel("Alert Limit:");
        alertValueLabel.setBounds(20, 260, 100, 25);
        editPanel.add(alertValueLabel);

        alertValue = new JTextField();
        alertValue.setText("0");
        alertValue.setBounds(100, 260, 110, 25);
        editPanel.add(alertValue);

        alertFlag = new JCheckBox("ON/OFF");
        alertFlag.setBounds(220, 260, 75, 25);
        editPanel.add(alertFlag);
        alertFlag.setSelected(true);

        //label printing
        JLabel printCountLabel = new JLabel("# of labels to print:");
        printCountLabel.setBounds(20, 320, 140, 25);
        editPanel.add(printCountLabel);

        printCount = new JTextField();
        printCount.setText("0");
        printCount.setBounds(140, 320, 70, 25);
        editPanel.add(printCount);

        print = new JButton("Print");
        print.setBounds(215, 320, 80, 25);
        print.addActionListener(this);
        editPanel.add(print);

        //external bare code
        bareCodeValue = new JTextField();
        bareCodeValue.setBounds(175, 290, 120, 25);
        editPanel.add(bareCodeValue);
        //bareCodeValue.setVisible(false);

        bareCodeFlag = new JCheckBox("Generate Bare Code");
        bareCodeFlag.setBounds(20, 290, 145, 25);
        bareCodeFlag.addItemListener(this);
        editPanel.add(bareCodeFlag);
        bareCodeFlag.setSelected(true);

        //action buttons
        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(210, 360);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(40, 360);
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
            bareCodeValue.setEnabled(false);
            bareCodeValue.setVisible(true);
//            if (!product.isGenerateBareCodeFlag()) {
//                bareCodeValue.setText(product.getBareCode());
//                bareCodeValue.setVisible(true);
//            }
        } else {
            name.setText("");
            if (lastUsedProduct != null) {
                name.setText(lastUsedProduct.getName());
            }
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
            currentPage = FIRST_PAGE;
            currentCategory = (Category) categoryBox.getSelectedItem();
            lastPage = productService.countProductsByCategoryId(currentCategory.getId());

            try {
                loadProducts(currentCategory.getId(), currentPage);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() instanceof JButton) {
            try {
                if (e.getSource() == next && currentPage < lastPage) {
                    loadProducts(currentCategory.getId(), ++currentPage);
                } else if (e.getSource() == prev && currentPage > FIRST_PAGE) {
                    loadProducts(currentCategory.getId(), --currentPage);
                } else if (e.getSource() == last) {
                    currentPage = lastPage;
                    loadProducts(currentCategory.getId(), currentPage);
                } else if (e.getSource() == first) {
                    currentPage = FIRST_PAGE;
                    loadProducts(currentCategory.getId(), currentPage);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (e.getSource() == add) {
                command = "ADD";
                if (editPanel != null)
                    this.remove(editPanel);
                this.repaint();
                edit(null);
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
                        command = "EDIT";
                        edit(product);
                        loadSaleHistory(product);
                        loadSupplyHistory(product);
                        editedProduct = product.clone();
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
                            loadProducts(currentCategory.getId(), currentPage);
                        } catch (ServiceException e1) {
                            Message.show(e1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "Product deleted.");
                        if (editPanel != null) {
                            this.remove(editPanel);
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

            } else if (e.getSource() == browse) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
                JFileChooser chooser = new JFileChooser();
                ImagePreviewPanel preview = new ImagePreviewPanel();
                chooser.setAccessory(preview);
                chooser.addPropertyChangeListener(preview);

                int answer = chooser.showOpenDialog(ProductManagement.this);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    imagePath.setText(file.getAbsolutePath());
                }
                return;
            }
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

        if (e.getSource() instanceof ImageButton) {
            ImageButton trigger = (ImageButton) e.getSource();
            if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
                this.remove(editPanel);
                if (supplyScrollPane != null) {
                    this.remove(supplyLabel);
                    this.remove(salesLabel);
                    this.remove(supplyScrollPane);
                    this.remove(salesScrollPane);
                }
                this.repaint();
            } else if (trigger.getCommand().equals(ButtonEnum.BARCODEHISTORY.getCommand())){
                this.remove(editPanel);
                this.repaint();
                try {
                    String barcodeStr = bcSearch.getText();
                    if (barcodeStr == "") {
                        JOptionPane.showMessageDialog(null, "Enter Barcode!");
                    } else {
                        Product product = productService.getProductByCode(barcodeStr);

                        loadSupplyHistory(product);
                        loadSaleHistory(product);
                        edit(product);

                        bcSearch.setText("");
                    }
                } catch (ServiceException e1) {
                    JOptionPane.showMessageDialog(null, "Product not found!");
                }
            } else if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
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
                if ("ADD".equals(command)) {
                    sb.append(FieldValidator.validateStringField("Image Path", imagePath.getText()));
                }
                if (sb.length() > 0) {
                    JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ("ADD".equals(command)) {
                    Product product = new Product();
                    product.setName(name.getText());
                    product.setCategoryId(currentCategory.getId());
                    product.setDescription(description.getText());
                    product.setCode(code.getText());
                    product.setGold(gold.isSelected());
                    product.setOther(other.isSelected());
                    product.setGenerateBareCodeFlag(bareCodeFlag.isSelected());
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

                    File file = new File(imagePath.getText());
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(file);
                    } catch (Exception ex) {
                        logger.error("Error loading image: " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "Error loading image file from disk.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    ProductUpdate iu = new ProductUpdate();
                    iu.setAddDate(new Date());
                    iu.setOperation(ADD);
                    if (other.isSelected()) {
                        iu.setQuantity(product.getQuantity());
                        iu.setWeight(0.0);
                    } else {
                        iu.setWeight(product.getWeight());
                        iu.setQuantity(0);
                    }

                    try {
                        productService.add(product, image);
                        iu.setProductId(product.getId());
                        productService.storeProductUpdate(iu);
                        lastUsedProduct = product;
                        JOptionPane.showMessageDialog(null, "Product added with success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadProducts(currentCategory.getId(), currentPage);
                    } catch (ServiceException se) {
                        JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    int count = 0;
                    try {
                        count = Integer.parseInt(printCount.getText());
                    } catch (NumberFormatException nfe) {
                        logger.error("Error parsing label count: " + nfe.getMessage());
                    }
                    PrintUtil.printLabel(product, count);

                } else if ("EDIT".equals(command)) {
                    int index = table.getSelectedRow();
                    if (index >= 0) {
                        String[] idArray = (String[]) table.getValueAt(index, 0);
                        int id = Integer.parseInt(idArray[0]);
                        Product product = null;
                        try {
                            product = productService.load(id);
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

                        BufferedImage image = null;
                        if (!Strings.isEmpty(imagePath.getText())) {
                            File file = new File(imagePath.getText());
                            try {
                                image = ImageIO.read(file);
                            } catch (Exception ex) {
                                logger.error("Error loading image: " + ex.getMessage());
                                JOptionPane.showMessageDialog(null, "Error loading image file from disk.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
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
                        try {
                            productService.update(product, image);
                            iu.setProductId(product.getId());
                            productService.storeProductUpdate(iu);
                            lastUsedProduct = product;
                            JOptionPane.showMessageDialog(null, "Product added with success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (ServiceException se) {
                            JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if ("SEARCH".equals(command)) {
                    try {
                        Product product = productService.getProductByCode(editedProduct.getBareCode());

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

                        BufferedImage image = null;
                        if (!Strings.isEmpty(imagePath.getText())) {
                            File file = new File(imagePath.getText());
                            try {
                                image = ImageIO.read(file);
                            } catch (Exception ex) {
                                logger.error("Error loading image: " + ex.getMessage());
                                JOptionPane.showMessageDialog(null, "Error loading image file from disk.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
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
                        iu.setProductId(product.getId());

                        productService.update(product, image);
                        productService.storeProductUpdate(iu);

                    } catch (ServiceException se) {
                        JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
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
