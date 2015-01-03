package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.StockDAO;
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
    public List<StockReport> getStockOnCategories() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "SELECT CAT.NAME,CAT.DESCRIPTION, SUM(PROD.QUANTITY) ITEMS, SUM(PROD.WEIGHT) WEIGHT, PROD.IS_GOLD, PROD.IS_OTHER,SUM((PROD.QUANTITY * PROD.PRICE))PRICE  FROM CATEGORIES CAT\n" +
                    "INNER JOIN PRODUCTS PROD ON PROD.CATEGORY_ID = CAT.ID\n" +
                    "WHERE PROD.quantity > 0\n" +
                    "GROUP BY CAT.NAME, CAT.ID";
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
}
