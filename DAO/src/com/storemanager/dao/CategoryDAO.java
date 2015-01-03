package com.storemanager.dao;

import com.storemanager.models.Category;
import com.storemanager.util.DAOException;

import java.util.List;

public interface CategoryDAO {

    public boolean store(Category category) throws DAOException;

    public boolean delete(Category category) throws DAOException;

    public List<Category> getCategoryList(int parentId) throws DAOException;

    public List<Category> getBaseCategoryList() throws DAOException;

    public List<Category> getAllCategories() throws DAOException;
}
