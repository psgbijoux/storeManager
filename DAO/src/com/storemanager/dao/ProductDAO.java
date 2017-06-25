package com.storemanager.dao;

import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.util.DAOException;

import java.util.ArrayList;
import java.util.List;

public interface ProductDAO {

    boolean store(Product product) throws DAOException;

    boolean delete(Product product) throws DAOException;

    List<Product> getProductsByCategoryId(int categoryId) throws DAOException;

    int countProductsByCategoryId(int categoryId);

    List<Product> getPaginatedProductsByCategory(int category, int page) throws DAOException;

    List<Product> getOutOfStockProductByCategoryId(int categoryId) throws DAOException;

    Product loadProduct(int productId) throws DAOException;

    Product getProductByBarCode(String barCode) throws DAOException;

    Product getProductByCode(String code) throws DAOException;

    Integer getBareCodeCount() throws DAOException;

    boolean storeProductUpdate(ProductUpdate productUpdate) throws DAOException;

    List<ProductUpdate> getProductUpdates(Product product) throws DAOException;

    public boolean delete(ProductUpdate productUpdate) throws DAOException;

    void updateProductDescription(ArrayList<String> barCodes, ArrayList<String> supplyCodes);
}
