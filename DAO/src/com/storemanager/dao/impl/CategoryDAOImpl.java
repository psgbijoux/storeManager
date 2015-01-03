package com.storemanager.dao.impl;

import com.storemanager.dao.CategoryDAO;
import com.storemanager.dao.DAO;
import com.storemanager.models.Category;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class CategoryDAOImpl extends DAO implements CategoryDAO {

    public CategoryDAOImpl() {
    }

    public boolean store(Category category) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(category);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Updated category: " + category.getName());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(Category category) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(category);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted categoryId: " + category.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Category> getCategoryList(int parentId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Category> result = (List<Category>) session.createCriteria(Category.class).add(Restrictions.eq("parentId", parentId)).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Category> getBaseCategoryList() throws DAOException {
        return this.getCategoryList(0);
    }

    public List<Category> getAllCategories() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Category> result = (List<Category>) session.createCriteria(Category.class).addOrder(Order.asc("parentId")).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
