package com.storemanager.dao;

import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.util.DAOException;

import java.util.List;

public interface ProductDAO {
    public boolean store(Product product) throws DAOException;

    public boolean delete(Product product) throws DAOException;

    public List<Product> getProductByCategoryId(int categoryId) throws DAOException;

    public List<Product> getOutOfStockProductByCategoryId(int categoryId) throws DAOException;

    public Product loadProduct(int productId) throws DAOException;

    public Product getProductByBarCode(String barCode) throws DAOException;

    public Integer getBareCodeCount() throws DAOException;

    public boolean storeProductUpdate(ProductUpdate productUpdate) throws DAOException;

    public List<ProductUpdate> getProductUpdates(Product product) throws DAOException;
}
