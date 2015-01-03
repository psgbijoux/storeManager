package com.storemanager.dao;

import com.storemanager.models.Product;
import com.storemanager.models.Sale;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;

import java.util.List;

public interface SaleDao {

    public boolean addNew(Sale sale) throws DAOException;

    public List<SaleDetail> getProductSales(Product product) throws DAOException;
}
