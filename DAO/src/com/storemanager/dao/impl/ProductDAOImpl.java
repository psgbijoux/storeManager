package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

            List<ProductUpdate> supplies = (List<ProductUpdate>) session.createCriteria(ProductUpdate.class)
                    .add(Restrictions.eq("productId", product.getId())).list();
            List<SaleDetail> sales = (List<SaleDetail>) session.createCriteria(SaleDetail.class)
                    .add(Restrictions.eq("productId", product.getId())).list();

            for(ProductUpdate pu: supplies) {
                session.delete(pu);
            }
            if (CollectionUtils.isEmpty(sales)) {
                session.delete(product);
            } else {
                for (SaleDetail sd : sales) {
                    session.delete(sd);
                }
            }

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

    public List<Product> getProductsByCategoryId(int categoryId) throws DAOException {
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

    @Override
    public int countProductsByCategoryId(int category) {
        final Session session = getSessionFactory().openSession();
        try {
            session.beginTransaction();
            Criteria cr = session.createCriteria(Product.class);
            cr.add(Restrictions.eq("categoryId", category));
            cr.setProjection(Projections.rowCount());
            Number no = (Number) cr.uniqueResult();
            return no.intValue() / PAGE_SIZE + 1;
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<Product> getPaginatedProductsByCategory(int category, int page) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            session.beginTransaction();
            Criteria cr = session.createCriteria(Product.class);
            cr.add(Restrictions.eq("categoryId", category));
            cr.setFirstResult((page-1)*PAGE_SIZE);
            cr.setMaxResults(PAGE_SIZE);
            return cr.list();
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

    public Product getProductByCode(String code) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Product result = (Product) session.createCriteria(Product.class).add(Restrictions.eq("bareCode", code)).uniqueResult();
            if (result == null) {
                result = (Product) session.createCriteria(Product.class).add(Restrictions.eq("code", code)).uniqueResult();
            }
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

    @Override
    public boolean delete(ProductUpdate productUpdate) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(productUpdate);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted product-update with id: " + productUpdate.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }


    public void updateProductDescription(ArrayList<String> barCodes, ArrayList<String> supplyCodes) {

        final Session session = getSessionFactory().openSession();
        System.out.println("START PROCESS");

        int index = 0;
        for (int i = 0; i < barCodes.size(); i++) {
            System.out.println(i + ": process product: " + barCodes.get(i));

            Product product = (Product) session.createCriteria(Product.class).add(Restrictions.eq("bareCode", barCodes.get(i))).uniqueResult();

            if (product != null) {
                product.setCode(supplyCodes.get(i));

                session.beginTransaction();
                session.saveOrUpdate(product);
                session.getTransaction().commit();
                System.out.println("product updated: " + barCodes.get(i));
                index++;
            }
        }

        System.out.println("END PROCESS: " + index);

        session.close();
        closeSessionFactory();
    }
}
