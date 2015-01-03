package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.util.DAOException;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class ProductDAOImpl extends DAO implements ProductDAO {

    public ProductDAOImpl() {
    }

    public boolean store(Product product) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(product);
            tx.commit();
            LOGGER.info(this.getClass().getName(), "Store product with id: " + product.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(Product product) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(product);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted product with id: " + product.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Product> getProductByCategoryId(int categoryId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Product> result = (List<Product>) session.createCriteria(Product.class).add(Restrictions.eq("categoryId", categoryId)).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Product> getOutOfStockProductByCategoryId(int categoryId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Product> result = (List<Product>) session.createCriteria(Product.class).add(Restrictions.eq("categoryId", categoryId)).add(Restrictions.geProperty("alertValue", "quantity")).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Product loadProduct(int productId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Product result = (Product) session.createCriteria(Product.class).add(Restrictions.eq("id", productId)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Product getProductByBarCode(String barCode) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Product result = (Product) session.createCriteria(Product.class).add(Restrictions.eq("bareCode", barCode)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Integer getBareCodeCount() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select max(id) from Products";
            SQLQuery query = session.createSQLQuery(sql);
            return (Integer) query.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public boolean storeProductUpdate(ProductUpdate productUpdate) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(productUpdate);
            tx.commit();
            LOGGER.info(this.getClass().getName(), "Store productUpdate for product with Id: " + productUpdate.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<ProductUpdate> getProductUpdates(Product product) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Criteria criteria = session.createCriteria(ProductUpdate.class);
            criteria.add(Restrictions.eq("productId", product.getId()));
            List<ProductUpdate> result = (List<ProductUpdate>) criteria.list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
