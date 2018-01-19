package com.storemanager.screens.management;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.*;
import com.storemanager.renderers.MultiLineTableCellRenderer;
import com.storemanager.screens.products.ImagePreviewPanel;
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

public abstract class AbstractManagementScreen extends AbstractPanel implements ItemListener {

    private StoreLogger logger = StoreLogger.getInstance(AbstractManagementScreen.class);
    private static final int FIRST_PAGE = 1;

    protected static final String ADD = "ADD";
    protected static final String UPDATE = "UPDATE";

    protected JButton next, prev, first, last, close;
    protected JButton cancel, save, browse, print;
    protected JCheckBox alertFlag, bareCodeFlag;
    protected JComboBox<Role> categoryBox;
    protected JLabel supplyLabel, salesLabel;
    protected JPanel editPanel;
    protected JRadioButton gold, other;
    protected JScrollPane supplyScrollPane, salesScrollPane;
    protected JTable table, supplyTable, salesTable;
    protected JTextField name, code, quantity, price, description, imagePath;
    protected JTextField weight, alertValue, printCount, bareCodeValue;
    private Window baseWindow;

    protected int currentPage;
    protected int lastPage;

    protected String command;

    protected Category currentCategory;
    protected Product editedProduct;
    protected Product lastUsedProduct;

    private CategoryService categoryService = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
    private ProductService productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);

    abstract List<? extends Object> getPaginatedData(int currentPage, Product product);

    abstract int getLastPageNo();

    abstract Object[] generateTableRowData(Object object);

    public AbstractManagementScreen(Window baseWindow) {

        this.baseWindow = baseWindow;
        this.setSize(1200, 730);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(1080, 680);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        //UI Components generation
        generateCategoryBox();
        generatePaginationButtons();
    }

    private void generateCategoryBox() {
        JLabel roleLabel = new JLabel("Category:");
        roleLabel.setBounds(10, 3, 100, 30);
        this.add(roleLabel);

        try {
            List<Category> categoryList = categoryService.getAllCategories();
            categoryBox = new JComboBox(categoryList.toArray());
            categoryBox.setBounds(70, 5, 200, 25);
            categoryBox.addActionListener(this);
            this.add(categoryBox);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving categories!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePaginationButtons() {
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
    }

    private void populateProductForm(Product product) {
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
    }

    private void emptyProductForm() {
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

    protected void loadSearchData(int currentPage, Product product) throws IOException {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Photo");
        tableModel.addColumn("Name");

        List<Object> paginatedData = (List<Object>) getPaginatedData(currentPage, product);
        for (Object data: paginatedData) {
            Object[] rowData = generateTableRowData(data);
            tableModel.addRow(rowData);
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
        table.getColumnModel().getColumn(0).setWidth(70);
        table.getColumnModel().getColumn(1).setWidth(180);
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

    protected void loadProductForm(Product product) {
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
            populateProductForm(product);
        } else {
            emptyProductForm();
        }
        this.add(editPanel);
        this.repaint();
    }

    protected void loadSupplyHistory(Product product) {

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

    protected void loadSaleHistory(Product product) {

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

    protected ImageIcon generateTableImageIcon(Product product) {
        InputStream in = new ByteArrayInputStream(product.getImage());
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage resizedImage = ImageUtil.resizeImage(bi, BufferedImage.TYPE_INT_ARGB, 200, 200);
        return new ImageIcon(resizedImage);
    }

    protected void updateProductByProductForm(Product product) {

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
    }

    protected BufferedImage updateImageByProductForm() {
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
        return image;
    }

    protected ProductUpdate computeUpdateByProductForm(Product product, Product editedProduct, String operation) {
        ProductUpdate iu = new ProductUpdate();
        iu.setAddDate(new Date());
        iu.setOperation(operation);
        if (other.isSelected()) {
            if (editedProduct != null) {
                iu.setQuantity(product.getQuantity() - editedProduct.getQuantity());
            } else {
                iu.setQuantity(product.getQuantity());
            }
            iu.setWeight(0.0);
        } else {
            if (editedProduct != null) {
                iu.setWeight(product.getWeight() - editedProduct.getWeight());
            } else {
                iu.setWeight(product.getWeight());
            }
            iu.setQuantity(0);
        }
        return iu;
    }

    protected String validateProductForm() {
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
        return sb.toString();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JComboBox && e.getSource() == categoryBox) {
            currentPage = FIRST_PAGE;
            currentCategory = (Category) categoryBox.getSelectedItem();
            lastPage = getLastPageNo();
            //disabling category filtering
            if (currentCategory.getId() == 11) {
                currentCategory = null;
            }
            try {
                loadSearchData(currentPage, null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (e.getSource() instanceof JButton) {
            try {
                if (e.getSource() == next && currentPage < lastPage) {
                    loadSearchData(++currentPage, null);
                } else if (e.getSource() == prev && currentPage > FIRST_PAGE) {
                    loadSearchData(--currentPage, null);
                } else if (e.getSource() == last) {
                    currentPage = lastPage;
                    loadSearchData(currentPage, null);
                } else if (e.getSource() == first) {
                    currentPage = FIRST_PAGE;
                    loadSearchData(currentPage, null);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (e.getSource() instanceof JButton) {
            if (e.getSource() == browse) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
                JFileChooser chooser = new JFileChooser();
                ImagePreviewPanel preview = new ImagePreviewPanel();
                chooser.setAccessory(preview);
                chooser.addPropertyChangeListener(preview);

                int answer = chooser.showOpenDialog(AbstractManagementScreen.this);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    imagePath.setText(file.getAbsolutePath());
                }
                return;
            } else if (e.getSource() == print) {
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

            if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
                baseWindow.closeScreen();
            }
            if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
                this.remove(editPanel);
                if (supplyScrollPane != null) {
                    this.remove(supplyLabel);
                    this.remove(salesLabel);
                    this.remove(supplyScrollPane);
                    this.remove(salesScrollPane);
                }
                this.repaint();
            } else if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
                String validationMessage = validateProductForm();
                if (!validationMessage.isEmpty()) {
                    JOptionPane.showMessageDialog(null, validationMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ("EDIT".equals(command)) {
                    int index = table.getSelectedRow();
                    if (index >= 0) {
                        String[] idArray = (String[]) table.getValueAt(index, 0);
                        int id = Integer.parseInt(idArray[0]);

                        try {
                            Product product = productService.load(id);
                            updateProductByProductForm(product);
                            BufferedImage image = updateImageByProductForm();
                            productService.update(product, image);

                            ProductUpdate productUpdate = computeUpdateByProductForm(product, editedProduct, UPDATE);
                            productUpdate.setProductId(product.getId());
                            productService.storeProductUpdate(productUpdate);

                            lastUsedProduct = product;
                            editedProduct = product;
                            JOptionPane.showMessageDialog(null, "Product saved with success!", "Success", JOptionPane.INFORMATION_MESSAGE);

                            loadSearchData(currentPage, null);
                            loadSupplyHistory(product);

                        } catch (ServiceException se) {
                            JOptionPane.showMessageDialog(null, se.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException ie) {
                            JOptionPane.showMessageDialog(null, ie.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            this.repaint();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
