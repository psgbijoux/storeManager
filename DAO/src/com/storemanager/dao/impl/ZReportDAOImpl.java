package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.ZReportDAO;
import com.storemanager.models.ZReport;
import com.storemanager.util.DAOException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZReportDAOImpl extends DAO implements ZReportDAO {

    @Override
    public List<ZReport> getZReportData(Date date) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select c.name category, p.is_gold is_gold, SUM(SD.quantity) quantity, SUM(SD.weight) weight, SUM(SD.price) price from sales S \n" +
                    "inner join sale_details SD on SD.sale_id = S.id\n" +
                    "inner join products P on SD.product_id = P.id\n" +
                    "inner join categories C on P.category_id = C.id\n" +
                    "where S.add_date=?\n" +
                    "group by category, p.is_gold";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, date);
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            List<ZReport> result = new ArrayList<ZReport>();
            while (rs.next()) {
                ZReport sr = new ZReport();
                int idx = 0;
                sr.setCategory(rs.get(idx++).toString());
                sr.setGold(Boolean.parseBoolean(rs.get(idx++).toString()));
                sr.setQuantity(Integer.parseInt(rs.get(idx++).toString()));
                sr.setWeight(Double.parseDouble(rs.get(idx++).toString()));
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
    public List<ZReport> getZReportData(Date startDate, Date endDate) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select c.name category, p.is_gold is_gold, SUM(SD.quantity) quantity, SUM(SD.weight) weight, SUM(SD.price) price from sales S \n" +
                    "inner join sale_details SD on SD.sale_id = S.id\n" +
                    "inner join products P on SD.product_id = P.id\n" +
                    "inner join categories C on P.category_id = C.id\n" +
                    "where S.add_date>=? and S.add_date<=?\n" +
                    "group by category, p.is_gold";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, startDate);
            query.setParameter(1, endDate);
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            List<ZReport> result = new ArrayList<ZReport>();
            while (rs.next()) {
                ZReport sr = new ZReport();
                int idx = 0;
                sr.setCategory(rs.get(idx++).toString());
                sr.setGold(Boolean.parseBoolean(rs.get(idx++).toString()));
                sr.setQuantity(Integer.parseInt(rs.get(idx++).toString()));
                sr.setWeight(Double.parseDouble(rs.get(idx++).toString()));
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
