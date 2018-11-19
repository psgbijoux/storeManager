package com.storemanager.dao;

import com.storemanager.models.StockReport;
import com.storemanager.util.DAOException;

import java.util.List;

public interface StockDAO {

    List<StockReport> getStockOnCategories(Boolean onlyGold) throws DAOException;

    List<String[]> getStockOnProducts() throws DAOException;

    List<String[]> getProductsInStockFromCategory(final Integer categoryId) throws DAOException;
}
