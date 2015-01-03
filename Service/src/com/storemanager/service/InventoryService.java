package com.storemanager.service;

import com.storemanager.dao.InventoryDAO;
import com.storemanager.dao.impl.InventoryDAOImpl;
import com.storemanager.dao.ReportDAO;
import com.storemanager.dao.impl.ReportDAOImpl;
import com.storemanager.models.Inventory;
import com.storemanager.models.InventoryReport;
import com.storemanager.models.Report;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import com.storemanager.util.reports.InventoryReportGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class InventoryService implements StoreService {
    private InventoryDAO dao;
    private ReportDAO reportDAO;

    @Autowired
    public InventoryService(InventoryDAOImpl dao, ReportDAOImpl reportDAO) {
        this.dao = dao;
        this.reportDAO = reportDAO;
    }

    public void addInventory(Inventory inventory) throws ServiceException {
        try {
            dao.add(inventory);
        } catch (DAOException e) {
            throw new ServiceException("Error adding Inventory entry for product: " + inventory.getProductName());
        }
    }

    public void clear() throws ServiceException {
        try {
            dao.clear();
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void generateInventoryReport() throws ServiceException {
        List<InventoryReport> data = null;
        try {
            data = dao.getInventoryResult();
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
}
