package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.StockDAO;
import com.storemanager.models.InventoryReport;
import com.storemanager.models.StockReport;
import com.storemanager.util.DAOException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StockDAOImpl extends DAO implements StockDAO {

    @Override
    public List<StockReport> getStockOnCategories(Boolean onlyGold) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql;
            if (onlyGold) {
                sql = "SELECT CAT.NAME,CAT.DESCRIPTION, SUM(PROD.QUANTITY) ITEMS, SUM(PROD.WEIGHT) WEIGHT, PROD.IS_GOLD, PROD.IS_OTHER,SUM((PROD.QUANTITY * PROD.PRICE))PRICE  FROM CATEGORIES CAT\n" +
                    "INNER JOIN PRODUCTS PROD ON PROD.CATEGORY_ID = CAT.ID\n" +
                    "WHERE PROD.quantity > 0 AND PROD.IS_GOLD = 1\n" +
                    "GROUP BY CAT.NAME, CAT.ID, PROD.IS_GOLD, PROD.IS_OTHER";
            } else {
                sql = "SELECT CAT.NAME,CAT.DESCRIPTION, SUM(PROD.QUANTITY) ITEMS, SUM(PROD.WEIGHT) WEIGHT, PROD.IS_GOLD, PROD.IS_OTHER,SUM((PROD.QUANTITY * PROD.PRICE))PRICE  FROM CATEGORIES CAT\n" +
                    "INNER JOIN PRODUCTS PROD ON PROD.CATEGORY_ID = CAT.ID\n" +
                    "WHERE PROD.quantity > 0\n" +
                    "GROUP BY CAT.NAME, CAT.ID";
            }
            Query query = session.createSQLQuery(sql);
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            List<StockReport> result = new ArrayList<StockReport>();
            while (rs.next()) {
                StockReport sr = new StockReport();
                int idx = 0;
                sr.setCategoryName(rs.get(idx++).toString() + " - " + rs.get(idx++).toString());
                sr.setItems(Integer.parseInt(rs.get(idx++).toString()));
                BigDecimal weight = new BigDecimal(rs.get(idx++).toString());
                weight = weight.setScale(2, RoundingMode.HALF_UP);
                sr.setWeight(weight.doubleValue());
                sr.setGold(Boolean.parseBoolean(rs.get(idx++).toString()));
                sr.setOther(Boolean.parseBoolean(rs.get(idx++).toString()));
                String priceValue = rs.get(idx++).toString();
                sr.setPrice(new BigDecimal(priceValue));
                result.add(sr);
            }
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<String[]> getStockOnProducts() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {

            String sql= "SELECT P.NAME,P.BARE_CODE, P.QUANTITY, P.PRICE, P.PRICE*P.QUANTITY AS 'TOTAL' FROM PRODUCTS P WHERE P.QUANTITY > 0 AND P.IS_OTHER = 1 ORDER BY P.NAME";
            Query query = session.createSQLQuery(sql);
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            List<String[]> result = new ArrayList<>();
            while (rs.next()) {
                String[] lineData = new String[5];
                int idx = 0;

                lineData[0] = rs.get(idx++).toString();
                lineData[1] = rs.get(idx++).toString();
                lineData[2] = rs.get(idx++).toString();
                lineData[3] = rs.get(idx++).toString();
                lineData[4] = rs.get(idx++).toString();
                result.add(lineData);
            }
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
