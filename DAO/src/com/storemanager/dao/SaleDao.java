package com.storemanager.dao;

import com.storemanager.models.Product;
import com.storemanager.models.Sale;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;

import java.util.List;

public interface SaleDao {

    boolean addNew(Object sale) throws DAOException;

    boolean delete(Sale sale) throws DAOException;

    boolean delete(SaleDetail saleDetail) throws DAOException;

    List<SaleDetail> getProductSales(Product product) throws DAOException;
}
