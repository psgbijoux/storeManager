package com.storemanager.service;

import com.storemanager.dao.ReportDAO;
import com.storemanager.dao.impl.ReportDAOImpl;
import com.storemanager.models.ReportData;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import com.storemanager.util.reports.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportService implements StoreService {
    private ReportDAO dao;

    @Autowired
    public ReportService(ReportDAOImpl dao) {
        this.dao = dao;
    }

    public void generateSaleReport(Date startDate, Date endDate) throws ServiceException {
        String reportName = "Raport Vanzare Detaliat";
        String[] header = {"Nume Produs", "Cod de Bare", "Data", "Buc.", "Pret unitar", "Valoare totala", "Discount procent.", "Discount valoric", "Valoare finala"};

        SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
        String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
        File pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdir();
        }
        Date createDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String fileName = "SalesReport_" + df.format(createDate) + ".pdf";

        ReportData data;
        if (endDate != null) {
            try {
                data = dao.getSalesData(startDate, endDate);
            } catch (DAOException e) {
                throw new ServiceException("Error generating report data for interval: [" + startDate + ", " + endDate + "]");
            }
        } else {
            try {
                data = dao.getSalesData(startDate);
            } catch (DAOException e) {
                throw new ServiceException("Error generating data starting with: " + startDate);
            }
        }
        data.setHeader(header);

        try {
            ReportGenerator.create(path + "\\" + fileName, reportName, data);
            Desktop.getDesktop().open(new File(path + "\\" + fileName));
        } catch (Exception e) {
            logger.error("Error generating report: " + e.getMessage());
        }
    }
}
