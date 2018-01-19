package com.storemanager.screens.management;

import com.storemanager.components.Window;
import com.storemanager.models.*;
import com.storemanager.service.*;
import com.storemanager.util.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesManagementScreen extends AbstractManagementScreen {

    private static final int FIRST_PAGE = 1;

    private JButton preview, refresh;
    private JButton dateIntervalSearchButton;
    private JTextField dateIntervalSearch;

    private String command;

    private Date startDate;
    private Date endDate;

    private ProductService productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
    private SaleService saleService = ServiceLocator.getService(ServiceName.SALE_SERVICE);

    public SalesManagementScreen(Window baseWindow) {

        super(baseWindow);

        preview = new JButton("preview");
        preview.setBounds(500, 5, 80, 20);
        preview.addActionListener(this);
        this.add(preview);

        refresh = new JButton("refresh");
        refresh.setBounds(585, 5, 80, 20);
        refresh.addActionListener(this);
        this.add(refresh);

        JLabel dateIntervalLabel = new JLabel("Date Interval:");
        dateIntervalLabel.setBounds(800, 5, 100, 25);
        this.add(dateIntervalLabel);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateIntervalSearch = new JTextField(df.format(new Date()) + "-" + df.format(new Date()));
        dateIntervalSearch.setLocation(900, 3);
        dateIntervalSearch.setSize(200, 25);
        this.add(dateIntervalSearch);

        dateIntervalSearchButton = new JButton("Search");
        dateIntervalSearchButton.setBounds(1100, 5, 80, 20);
        dateIntervalSearchButton.addActionListener(this);
        this.add(dateIntervalSearchButton);
    }

    protected void updateTimelineInterval() {
        String[] intervals = dateIntervalSearch.getText().split("-");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            startDate = df.parse(intervals[0]);
            endDate = df.parse(intervals[1]);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error with the date interval!","Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected List<SaleDetail> getPaginatedData(int currentPage, Product product) {
        List<SaleDetail> sales = new ArrayList<>();
        try {
            sales.addAll(saleService.getSalesFilteredByDate(startDate, endDate, currentPage, currentCategory));
        } catch (ServiceException e) {
            Message.show(e);
        }
        return sales;
    }

    @Override
    protected int getLastPageNo() {
        int lastPage = currentPage;
        try {
            lastPage = saleService.countSalesFilteredByDate(startDate, endDate, currentCategory);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Error loading last page.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lastPage;
    }

    @Override
    protected Object[] generateTableRowData(Object object) {
        SaleDetail saleDetail = (SaleDetail) object;
        Product product = saleDetail.getProduct();
        DateFormat df = new SimpleDateFormat("dd/MM/yy");

        String[] id = new String[3];
        id[0] = String.valueOf(product.getId());
        id[1] = String.valueOf(saleDetail.getSaleId());
        id[2] = df.format(saleDetail.getAddDate());

        String[] info = new String[5];
        info[0] = product.getName() + " - " + product.getCode();
        info[1] = product.isGold() ? product.getDescription() + " | weight: " + product.getWeight() : product.getDescription();
        info[2] = "cod bare: " + product.getBareCode();
        info[3] = "price: " + String.valueOf(product.getPrice()) + " | sale: " + String.valueOf(saleDetail.getPrice());
        info[4] = "stock: " + String.valueOf(product.getQuantity()) + " | sale: " + String.valueOf(saleDetail.getQuantity());

        Object[] data = new Object[3];
        data[0] = id;
        data[1] = info;
        data[2] = generateTableImageIcon(product);
        return data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        if (e.getSource() instanceof JButton) {
            if (e.getSource() == dateIntervalSearchButton || e.getSource() == refresh) {
                updateTimelineInterval();
                currentPage = FIRST_PAGE;
                lastPage = getLastPageNo();
                try {
                    loadSearchData(currentPage, null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == preview) {
                if (editPanel != null)
                    this.remove(editPanel);
                int index = super.table.getSelectedRow();
                if (index >= 0) {
                    String[] idArray = (String[]) table.getValueAt(index, 0);
                    int id = Integer.parseInt(idArray[0]);
                    try {
                        Product product = productService.load(id);
                        loadProductForm(product);
                        loadSaleHistory(product);
                        loadSupplyHistory(product);

                        super.command = "EDIT";
                        super.editedProduct = product.clone();
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
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
