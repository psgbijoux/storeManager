package com.storemanager.service;

import com.storemanager.dao.CategoryDAO;
import com.storemanager.dao.impl.CategoryDAOImpl;
import com.storemanager.models.Category;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements StoreService {
    private CategoryDAO dao;

    @Autowired
    public CategoryService(CategoryDAOImpl dao) {
        this.dao = dao;
    }

    public List<Category> getCategoryList(Category category) throws ServiceException {
        try {
            if (category == null) {
                return dao.getBaseCategoryList();
            } else {
                return dao.getCategoryList(category.getId());
            }
        } catch (DAOException e) {
            throw new ServiceException("Error getting category list.");
        }
    }

    public List<Category> getAllCategories() throws ServiceException {
        try {
            return dao.getAllCategories();
        } catch (DAOException e) {
            throw new ServiceException("Error getting all category list");
        }
    }

    public boolean update(Category category) throws ServiceException {
        try {
            return dao.store(category);
        } catch (DAOException e) {
            throw new ServiceException("Error updating category: " + category.getName());
        }
    }

    public boolean add(Category category) throws ServiceException {
        try {
            return dao.store(category);
        } catch (DAOException e) {
            throw new ServiceException("Error saving category: " + category.getName());
        }
    }

    public boolean delete(Category category) throws ServiceException {
        try {
            return dao.delete(category);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting category: " + category.getName());
        }
    }
}
