package com.storemanager.dao;

import com.storemanager.models.ZReport;
import com.storemanager.util.DAOException;

import java.util.Date;
import java.util.List;

public interface ZReportDAO {
    List<ZReport> getZReportData(Date date) throws DAOException;

    List<ZReport> getZReportData(Date startDate, Date endDate) throws DAOException;
}
