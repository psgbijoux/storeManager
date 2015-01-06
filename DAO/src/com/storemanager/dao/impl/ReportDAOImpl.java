package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.ReportDAO;
import com.storemanager.models.Report;
import com.storemanager.models.ReportData;
import com.storemanager.models.Sale;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportDAOImpl extends DAO implements ReportDAO {
    public ReportDAOImpl() {
    }

    @Override
    public ReportData getSalesData(Date date) throws DAOException {
        ReportData reportData = new ReportData();
        final Session session = getSessionFactory().openSession();
        try {
            List<String[]> data = new ArrayList<String[]>();
            List<Sale> result = (List<Sale>) session.createCriteria(Sale.class).add(Restrictions.eq("addDate", date)).list();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            BigDecimal total = new BigDecimal(0.0);
            BigDecimal discount = new BigDecimal(0.0);
            total = total.setScale(2, RoundingMode.HALF_UP);
            discount = discount.setScale(2, RoundingMode.HALF_UP);
            for (Sale sale : result) {
                for (SaleDetail detail : sale.getDetails()) {

                    BigDecimal productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                    BigDecimal productDiscountPrice;

                    if(detail.getWeight() == 0.0) {
                        if (!detail.getDiscountPrice().toString().equals("0.00")) {
                            productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                            discount = discount.add(productDiscountPrice);
                        } else {
                            productDiscountPrice = new BigDecimal(0.0);
                        }
                    } else {
                        if (!detail.getDiscountPrice().toString().equals("0.00")) {
                            productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getWeight()));
                            productFullPrice = productFullPrice.setScale(2, RoundingMode.HALF_UP);
                            productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                            productDiscountPrice = productDiscountPrice.setScale(2, RoundingMode.HALF_UP);
                            discount = discount.add(productDiscountPrice);
                        } else {
                            productDiscountPrice = new BigDecimal(0.0);
                        }
                    }

                    String[] lineData = new String[9];
                    lineData[0] = detail.getProduct().getName();
                    lineData[1] = detail.getProduct().getBareCode();
                    lineData[2] = df.format(sale.getAddDate());
                    lineData[3] = Double.toString(detail.getQuantity());
                    lineData[4] = Double.toString(detail.getUnitPrice().doubleValue());
                    lineData[5] = Double.toString(productFullPrice.doubleValue());
                    lineData[6] = Integer.toString(detail.getDiscount()) + " %";
                    lineData[7] = Double.toString(productDiscountPrice.doubleValue());
                    lineData[8] = Double.toString(detail.getPrice().doubleValue());
                    data.add(lineData);

                    total = total.add(detail.getPrice());
                }
            }

            reportData.setData(data);
            Map<String, String> footer = new HashMap<String, String>();
            footer.put("Total", Double.toString(total.doubleValue()));
            footer.put("Discount", Double.toString(discount.doubleValue()));
            reportData.setFooter(footer);
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
        return reportData;
    }


    public ReportData getSalesData(Date startDate, Date endDate) throws DAOException {
        ReportData reportData = new ReportData();

        final Session session = getSessionFactory().openSession();
        try {
            List<String[]> data = new ArrayList<String[]>();
            List<Sale> result = (List<Sale>) session.createCriteria(Sale.class).add(Restrictions.ge("addDate", startDate))
                    .add(Restrictions.le("addDate", endDate)).list();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            BigDecimal total = new BigDecimal(0.0);
            BigDecimal discount = new BigDecimal(0.0);
            total = total.setScale(2, RoundingMode.HALF_UP);
            discount = discount.setScale(2, RoundingMode.HALF_UP);
            for (Sale sale : result) {
                for (SaleDetail detail : sale.getDetails()) {


                    BigDecimal productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                    BigDecimal productDiscountPrice;

                    if(detail.getWeight() == 0.0) {
                        if (!detail.getDiscountPrice().toString().equals("0.00")) {
                            productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                            discount = discount.add(productDiscountPrice);
                        } else {
                            productDiscountPrice = new BigDecimal(0.0);
                        }
                    } else {
                        if (!detail.getDiscountPrice().toString().equals("0.00")) {
                            productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getWeight()));
                            productFullPrice = productFullPrice.setScale(2, RoundingMode.HALF_UP);
                            productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                            productDiscountPrice = productDiscountPrice.setScale(2, RoundingMode.HALF_UP);
                            discount = discount.add(productDiscountPrice);
                        } else {
                            productDiscountPrice = new BigDecimal(0.0);
                        }
                    }

                    String[] lineData = new String[9];
                    lineData[0] = detail.getProduct().getName();
                    lineData[1] = detail.getProduct().getBareCode();
                    lineData[2] = df.format(sale.getAddDate());
                    lineData[3] = Double.toString(detail.getQuantity());
                    lineData[4] = Double.toString(detail.getUnitPrice().doubleValue());
                    lineData[5] = Double.toString(productFullPrice.doubleValue());
                    lineData[6] = Integer.toString(detail.getDiscount()) + " %";
                    lineData[7] = Double.toString(productDiscountPrice.doubleValue());
                    lineData[8] = Double.toString(detail.getPrice().doubleValue());
                    data.add(lineData);

                    total = total.add(detail.getPrice());
                }
            }
            reportData.setData(data);
            Map<String, String> footer = new HashMap<String, String>();
            footer.put("Total", Double.toString(total.doubleValue()));
            footer.put("Discount", Double.toString(discount.doubleValue()));
            reportData.setFooter(footer);
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
        return reportData;
    }

    @Override
    public ReportData getDiscountSalesData(Date date) throws DAOException {
        ReportData reportData = new ReportData();
        final Session session = getSessionFactory().openSession();
        try {
            List<String[]> data = new ArrayList<String[]>();
            List<SaleDetail> result = (List<SaleDetail>) session.createCriteria(SaleDetail.class)
                    .add(Restrictions.eq("addDate", date)).add(Restrictions.gt("discount", 0)).list();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            BigDecimal productFullPrice;
            BigDecimal productDiscountPrice;

            BigDecimal total = new BigDecimal(0.0);
            BigDecimal discount = new BigDecimal(0.0);
            total = total.setScale(2, RoundingMode.HALF_UP);
            discount = discount.setScale(2, RoundingMode.HALF_UP);
            for (SaleDetail detail : result) {


                if(detail.getWeight() == 0.0) {
                    productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                    productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                    discount = discount.add(productDiscountPrice);
                } else {
                    productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getWeight()));
                    productFullPrice = productFullPrice.setScale(2, RoundingMode.HALF_UP);
                    productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                    productDiscountPrice = productDiscountPrice.setScale(2, RoundingMode.HALF_UP);
                    discount = discount.add(productDiscountPrice);
                }

                String[] lineData = new String[9];
                lineData[0] = detail.getProduct().getName();
                lineData[1] = detail.getProduct().getBareCode();
                lineData[2] = df.format(detail.getAddDate());
                lineData[3] = Double.toString(detail.getQuantity());
                lineData[4] = Double.toString(detail.getUnitPrice().doubleValue());
                lineData[5] = Double.toString(productFullPrice.doubleValue());
                lineData[6] = Integer.toString(detail.getDiscount()) + " %";
                lineData[7] = Double.toString(productDiscountPrice.doubleValue());
                lineData[8] = Double.toString(detail.getPrice().doubleValue());
                data.add(lineData);

                total = total.add(detail.getPrice());
            }

            reportData.setData(data);
            Map<String, String> footer = new HashMap<String, String>();
            footer.put("Total", Double.toString(total.doubleValue()));
            footer.put("Discount", Double.toString(discount.doubleValue()));
            reportData.setFooter(footer);
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
        return reportData;
    }

    @Override
    public ReportData getDiscountSalesData(Date startDate, Date endDate) throws DAOException {
        ReportData reportData = new ReportData();
        final Session session = getSessionFactory().openSession();
        try {
            List<String[]> data = new ArrayList<String[]>();
            List<SaleDetail> result = (List<SaleDetail>) session.createCriteria(SaleDetail.class)
                    .add(Restrictions.ge("addDate", startDate)).add(Restrictions.le("addDate", endDate))
                    .add(Restrictions.gt("discount", 0)).list();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            BigDecimal productFullPrice;
            BigDecimal productDiscountPrice;

            BigDecimal total = new BigDecimal(0.0);
            BigDecimal discount = new BigDecimal(0.0);
            total = total.setScale(2, RoundingMode.HALF_UP);
            discount = discount.setScale(2, RoundingMode.HALF_UP);
            for (SaleDetail detail : result) {


                if(detail.getWeight() == 0.0) {
                    productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                    productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                    discount = discount.add(productDiscountPrice);
                } else {
                    productFullPrice = detail.getUnitPrice().multiply(new BigDecimal(detail.getWeight()));
                    productFullPrice = productFullPrice.setScale(2, RoundingMode.HALF_UP);
                    productDiscountPrice = productFullPrice.subtract(detail.getDiscountPrice());
                    productDiscountPrice = productDiscountPrice.setScale(2, RoundingMode.HALF_UP);
                    discount = discount.add(productDiscountPrice);
                }

                String[] lineData = new String[9];
                lineData[0] = detail.getProduct().getName();
                lineData[1] = detail.getProduct().getBareCode();
                lineData[2] = df.format(detail.getAddDate());
                lineData[3] = Double.toString(detail.getQuantity());
                lineData[4] = Double.toString(detail.getUnitPrice().doubleValue());
                lineData[5] = Double.toString(productFullPrice.doubleValue());
                lineData[6] = Integer.toString(detail.getDiscount()) + " %";
                lineData[7] = Double.toString(productDiscountPrice.doubleValue());
                lineData[8] = Double.toString(detail.getPrice().doubleValue());
                data.add(lineData);

                total = total.add(detail.getPrice());
            }

            reportData.setData(data);
            Map<String, String> footer = new HashMap<String, String>();
            footer.put("Total", Double.toString(total.doubleValue()));
            footer.put("Discount", Double.toString(discount.doubleValue()));
            reportData.setFooter(footer);
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
        return reportData;
    }

    @Override
    public boolean store(Report report) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(report);
            tx.commit();
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
