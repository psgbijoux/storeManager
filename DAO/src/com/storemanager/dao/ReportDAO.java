package com.storemanager.dao;

import com.storemanager.models.Report;
import com.storemanager.models.ReportData;
import com.storemanager.util.DAOException;

import java.util.Date;

public interface ReportDAO {
    public ReportData getSalesData(Date date) throws DAOException;

    public ReportData getSalesData(Date startDate, Date endDate) throws DAOException;

    public boolean store(Report report) throws DAOException;
}
