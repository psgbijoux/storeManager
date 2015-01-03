package com.storemanager.service;

import com.storemanager.dao.ReportDAO;
import com.storemanager.dao.impl.ReportDAOImpl;
import com.storemanager.dao.SupplyDAO;
import com.storemanager.dao.impl.SupplyDAOImpl;
import com.storemanager.models.Report;
import com.storemanager.models.SupplyReport;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import com.storemanager.util.reports.SupplyReportGenerator;
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
public class SupplyService implements StoreService {
    private SupplyDAO dao;
    private ReportDAO reportDAO;

    @Autowired
    public SupplyService(SupplyDAOImpl dao, ReportDAOImpl reportDAO) {
        this.dao = dao;
        this.reportDAO = reportDAO;
    }

    public void generateSupplyReport(Date startDate, Date endDate) throws ServiceException {
        List<SupplyReport> data = null;
        try {
            if (endDate == null) {
                data = dao.getSupplyReportData(startDate);
            } else {
                data = dao.getSupplyReportData(startDate, endDate);
            }
        } catch (DAOException e) {
            throw new ServiceException("Error retrieving supply report data.");
        }
        try {
            String reportName = "Supply Report";

            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdir();
            }
            Date createDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String fileName = path + "\\" + "SupplyReport_" + df.format(createDate) + ".pdf";

            SupplyReportGenerator.create(fileName, reportName, data);
            Report report = new Report();
            report.setName(reportName);
            report.setAddDate(new Date());
            FileInputStream fis = new FileInputStream(new File(fileName));
            report.setReport(IOUtils.toByteArray(fis));
            reportDAO.store(report);
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            throw new ServiceException("Error generating supply report.");
        }
    }

}
