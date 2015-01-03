package com.storemanager.dao;

import com.storemanager.models.SupplyReport;
import com.storemanager.util.DAOException;

import java.util.Date;
import java.util.List;

public interface SupplyDAO {
    public List<SupplyReport> getSupplyReportData(Date date) throws DAOException;

    public List<SupplyReport> getSupplyReportData(Date startDate, Date endDate) throws DAOException;
}
