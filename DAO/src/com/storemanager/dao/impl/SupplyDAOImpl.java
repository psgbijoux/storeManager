package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.SupplyDAO;
import com.storemanager.models.SupplyReport;
import com.storemanager.util.DAOException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupplyDAOImpl extends DAO implements SupplyDAO {
    @Override
    public List<SupplyReport> getSupplyReportData(Date date) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select P.name, P.bare_code, P.code, PU.operation, PU.add_date, PU.quantity, PU.weight, PU.quantity*P.price price from products P\n" +
                    "inner join product_update PU\n" +
                    "where PU.product_id = P.id\n" +
                    "and PU.add_date=?";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, date);
            List<SupplyReport> result = new ArrayList<SupplyReport>();
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            while (rs.next()) {
                SupplyReport supplyReport = new SupplyReport();
                int idx = 0;
                supplyReport.setProductName(rs.get(idx++).toString());
                supplyReport.setBareCode(rs.get(idx++).toString());
                supplyReport.setCode(rs.get(idx++).toString());
                supplyReport.setOperation(rs.get(idx++).toString());
                String dateString = rs.get(idx++).toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                supplyReport.setDate(df.parse(dateString));
                supplyReport.setQuantity(Integer.parseInt(rs.get(idx++).toString()));
                supplyReport.setWeight(Double.parseDouble(rs.get(idx++).toString()));
                supplyReport.setPrice(new BigDecimal(rs.get(idx++).toString()));
                result.add(supplyReport);
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
    public List<SupplyReport> getSupplyReportData(Date startDate, Date endDate) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select P.name, P.bare_code, P.code, PU.operation, PU.add_date, PU.quantity, PU.weight, PU.quantity*P.price price from products P\n" +
                    "inner join product_update PU\n" +
                    "where PU.product_id = P.id\n" +
                    "and PU.add_date>= ?\n" +
                    "and\n" +
                    "PU.add_date<= ?";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, startDate);
            query.setParameter(1, endDate);
            List<SupplyReport> result = new ArrayList<SupplyReport>();
            ScrollableResults rs = query.scroll(ScrollMode.FORWARD_ONLY);
            while (rs.next()) {
                SupplyReport supplyReport = new SupplyReport();
                int idx = 0;
                supplyReport.setProductName(rs.get(idx++).toString());
                supplyReport.setBareCode(rs.get(idx++).toString());
                supplyReport.setCode(rs.get(idx++).toString());
                supplyReport.setOperation(rs.get(idx++).toString());
                String dateString = rs.get(idx++).toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                supplyReport.setDate(df.parse(dateString));
                supplyReport.setQuantity(Integer.parseInt(rs.get(idx++).toString()));
                supplyReport.setWeight(Double.parseDouble(rs.get(idx++).toString()));
                supplyReport.setPrice(new BigDecimal(rs.get(idx++).toString()));
                result.add(supplyReport);
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
