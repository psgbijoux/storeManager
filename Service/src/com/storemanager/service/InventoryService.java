package com.storemanager.service;

import com.storemanager.dao.InventoryDAO;
import com.storemanager.dao.ReportDAO;
import com.storemanager.dao.StockDAO;
import com.storemanager.dao.impl.InventoryDAOImpl;
import com.storemanager.dao.impl.ReportDAOImpl;
import com.storemanager.dao.impl.StockDAOImpl;
import com.storemanager.models.*;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import com.storemanager.util.reports.InventoryGoldReportGenerator;
import com.storemanager.util.reports.InventoryProductsReportGenerator;
import com.storemanager.util.reports.InventoryReportGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InventoryService implements StoreService {
    private InventoryDAO dao;
    private StockDAO stockDAO;
    private ReportDAO reportDAO;

    @Autowired
    public InventoryService(InventoryDAOImpl dao, ReportDAOImpl reportDAO, StockDAOImpl stockDAO) {
        this.dao = dao;
        this.stockDAO = stockDAO;
        this.reportDAO = reportDAO;
    }

    public void addInventory(Inventory inventory) throws ServiceException {
        try {
            dao.add(inventory);
        } catch (DAOException e) {
            throw new ServiceException("Error adding Inventory entry for product: " + inventory.getProductName());
        }
    }

    public void addInventoryFirst(InventoryFirst inventory) throws ServiceException {
        try {
            dao.addFirst(inventory);
        } catch (DAOException e) {
            throw new ServiceException("Error adding Inventory entry for product: " + inventory.getProductName());
        }
    }

    public void addInventorySecond(InventorySec inventory) throws ServiceException {
        try {
            dao.addSecond(inventory);
        } catch (DAOException e) {
            throw new ServiceException("Error adding Inventory entry for product: " + inventory.getProductName());
        }
    }

    public void clear(String inventoryType) throws ServiceException {
        try {
            dao.clear(inventoryType);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void generateInventoryReport(String inventoryType, Category category) throws ServiceException {
        List<InventoryReport> data = null;
        try {
            if (category != null) {
                data = dao.getCategoryInventoryResult(inventoryType, category);
            } else {
                data = dao.getInventoryResult(inventoryType);
            }
        } catch (DAOException e) {
            throw new ServiceException("Error retrieving inventory report data.");
        }
        try {
            String reportName = "Inventory Report";

            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdir();
            }
            Date createDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String fileName = path + "\\" + "InventoryReport_" + df.format(createDate) + ".pdf";

            InventoryReportGenerator.create(fileName, reportName, data);
            Report report = new Report();
            report.setName(reportName);
            report.setAddDate(new Date());
            FileInputStream fis = new FileInputStream(new File(fileName));
            report.setReport(IOUtils.toByteArray(fis));
            reportDAO.store(report);
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            throw new ServiceException("Error generating report.");
        }
    }

    public void generateGoldInventoryReport() throws ServiceException {
        List<StockReport> data = null;

        try {
            boolean onlyGold = true;
            data = stockDAO.getStockOnCategories(onlyGold);
        } catch (DAOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new ServiceException("Error with data on gold inventory report");
        }
        try {
            String reportName = "Inventory Report";

            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdir();
            }
            Date createDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String fileName = path + "\\" + "InventoryGoldReport_" + df.format(createDate) + ".pdf";

            InventoryGoldReportGenerator.create(fileName, reportName, data);
            Report report = new Report();
            report.setName(reportName);
            report.setAddDate(new Date());
            FileInputStream fis = new FileInputStream(new File(fileName));
            report.setReport(IOUtils.toByteArray(fis));
            reportDAO.store(report);
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            throw new ServiceException("Error generating report.");
        }
    }

    public void generateProductsInventoryReport() throws ServiceException {
        List<String[]> data = new ArrayList<>();

        try {
            data = stockDAO.getStockOnProducts();
        } catch (DAOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new ServiceException("Error with data on products inventory report");
        }
        try {
            String reportName = "Inventory Report";

            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdir();
            }
            Date createDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String fileName = path + "\\" + "InventoryProductsReport_" + df.format(createDate) + ".pdf";

            InventoryProductsReportGenerator.create(fileName, reportName, data);
            Report report = new Report();
            report.setName(reportName);
            report.setAddDate(new Date());
            FileInputStream fis = new FileInputStream(new File(fileName));
            report.setReport(IOUtils.toByteArray(fis));
            reportDAO.store(report);
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            throw new ServiceException("Error generating report.");
        }
    }

    public void generateGoldStockReport() throws ServiceException {
        final List<String[]> data1 = new ArrayList<>();
        final List<String[]> data2 = new ArrayList<>();
        final List<String[]> data3 = new ArrayList<>();
        final List<String[]> data4 = new ArrayList<>();
        final List<String[]> data5 = new ArrayList<>();
        final List<String[]> data6 = new ArrayList<>();

        try {
            data1.addAll(this.stockDAO.getProductsInStockFromCategory(14));
            data2.addAll(this.stockDAO.getProductsInStockFromCategory(15));
            data3.addAll(this.stockDAO.getProductsInStockFromCategory(16));
            data4.addAll(this.stockDAO.getProductsInStockFromCategory(17));
            data5.addAll(this.stockDAO.getProductsInStockFromCategory(25));
            data6.addAll(this.stockDAO.getProductsInStockFromCategory(26));
        } catch (DAOException e) {
            throw new ServiceException("Error generating report.");
        }
    }
}
