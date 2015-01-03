package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.SaleDao;
import com.storemanager.models.Product;
import com.storemanager.models.Sale;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class SaleDaoImpl extends DAO implements SaleDao {

    public SaleDaoImpl() {
    }

    @Override
    public boolean addNew(Sale sale) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.save(sale);
            tx.commit();
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<SaleDetail> getProductSales(Product product) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Criteria criteria = session.createCriteria(SaleDetail.class);
            criteria.add(Restrictions.eq("productId", product.getId()));
            List<SaleDetail> result = (List<SaleDetail>) criteria.list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
