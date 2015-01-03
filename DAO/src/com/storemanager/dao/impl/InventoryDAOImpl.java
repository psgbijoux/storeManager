package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.InventoryDAO;
import com.storemanager.models.Inventory;
import com.storemanager.models.InventoryReport;
import com.storemanager.util.DAOException;
import org.hibernate.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryDAOImpl extends DAO implements InventoryDAO {

    @Override
    public boolean add(Inventory inventory) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.save(inventory);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Inserted inventory entry: " + inventory.getProductId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public boolean clear() throws DAOException {
        final Session session = getSessionFactory().openSession();
        disableNewSessionFactory();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "delete from inventory";
            Query query = session.createSQLQuery(sql);
            query.executeUpdate();
            tx.commit();
            LOGGER.info(this.getClass().getName(), "Inventory table cleared. Date: " + new Date());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<InventoryReport> getInventoryResult() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = " SELECT * FROM(\n" +
                    "SELECT PROD.ID,PROD.NAME, PROD.BARE_CODE,PROD.CODE,PROD.QUANTITY QUANTITY, IFNULL(SUM(INV.QUANTITY), 0)  STOCK FROM PRODUCTS PROD\n" +
                    "LEFT OUTER JOIN INVENTORY INV\n" +
                    "ON PROD.ID = INV.PRODUCT_ID\n" +
                    "GROUP BY PROD.ID\n" +
                    ") R WHERE R.QUANTITY!=R.STOCK";
            Query query = session.createSQLQuery(sql);
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            List<InventoryReport> result = new ArrayList<InventoryReport>();
            while (rs.next()) {
                InventoryReport ir = new InventoryReport();
                int idx = 0;
                ir.setId(Integer.parseInt(rs.get(idx++).toString()));
                ir.setName(rs.get(idx++).toString());
                ir.setBareCode(rs.get(idx++).toString());
                ir.setCode(rs.get(idx++).toString());
                ir.setQuantity(Integer.parseInt(rs.get(idx++).toString()));
                ir.setStock(Integer.parseInt(rs.get(idx++).toString()));
                result.add(ir);
            }
            enableNewSessionFactory();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
