package com.storemanager.dao;

import com.storemanager.models.StockReport;
import com.storemanager.util.DAOException;

import java.util.List;

public interface StockDAO {
    public List<StockReport> getStockOnCategories(Boolean onlyGold) throws DAOException;

    public List<String[]> getStockOnProducts() throws DAOException;
}
