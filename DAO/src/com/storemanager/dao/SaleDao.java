package com.storemanager.dao;

import com.storemanager.models.Category;
import com.storemanager.models.Product;
import com.storemanager.models.Sale;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;

import java.util.Date;
import java.util.List;

public interface SaleDao {

    boolean addNew(Object sale) throws DAOException;

    boolean delete(Sale sale) throws DAOException;

    boolean delete(SaleDetail saleDetail) throws DAOException;

    List<SaleDetail> getProductSales(Product product) throws DAOException;

    List<SaleDetail> getPaginatedSalesFilterByDate(Date startDate, Date endDate, int page) throws DAOException;

    List<SaleDetail> getPaginatedSalesFilterByDateAndCategory(Date startDate, Date endDate, int page, Category category) throws DAOException;

    int countSalesFilteredByDate(Date startDate, Date endDate) throws DAOException;

    int countSalesFilteredByDateAndCategory(Date startDate, Date endDate, Category category) throws DAOException;

}
