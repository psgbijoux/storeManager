package com.storemanager.service;

import com.storemanager.dao.ReportDAO;
import com.storemanager.dao.impl.ReportDAOImpl;
import com.storemanager.dao.ZReportDAO;
import com.storemanager.dao.impl.ZReportDAOImpl;
import com.storemanager.models.Report;
import com.storemanager.models.ZReport;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import com.storemanager.util.reports.ZReportGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ZReportService implements StoreService {
    private ZReportDAO dao;
    private ReportDAO reportDAO;

    @Autowired
    public ZReportService(ZReportDAOImpl dao, ReportDAOImpl reportDAO) {
        this.dao = dao;
        this.reportDAO = reportDAO;
    }

    public void generateZReport(Date startDate, Date endDate) throws ServiceException {
        List<ZReport> data = null;
        try {
            if (endDate == null) {
                data = dao.getZReportData(startDate);
            } else {
                data = dao.getZReportData(startDate, endDate);
            }
        } catch (DAOException e) {
            throw new ServiceException("Error retrieving Z report data.");
        }
        try {
            String reportName = "Z Report";

            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            String path = settingsService.loadSettings(SettingsEnum.REPORTS_FOLDER).getValue();
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdir();
            }
            Date createDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String fileName = path + "\\" + "ZReport_" + df.format(createDate) + ".pdf";

            ZReportGenerator.create(fileName, reportName, data);
            Report report = new Report();
            report.setName(reportName);
            report.setAddDate(new Date());
            FileInputStream fis = new FileInputStream(new File(fileName));
            report.setReport(IOUtils.toByteArray(fis));
            reportDAO.store(report);
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            throw new ServiceException("Error generating Z report.");
        }
    }
}
