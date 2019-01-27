package com.storemanager.service;

import com.storemanager.dao.ProductDAO;
import com.storemanager.dao.impl.ProductDAOImpl;
import com.storemanager.dao.SaleDao;
import com.storemanager.dao.impl.SaleDaoImpl;
import com.storemanager.models.*;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SaleService implements StoreService {

    private static final String SETTINGS_PROPERTIES = "settings.properties";

    private SaleDao saleDao;
    private ProductDAO productDAO;
    private List<ScreenProduct> productList;

    @Autowired
    public SaleService(SaleDaoImpl saleDao, ProductDAOImpl productDAO) {
        this.saleDao = saleDao;
        this.productDAO = productDAO;
        productList = new ArrayList<ScreenProduct>();
    }

    public void add(ScreenProduct product) {
        this.productList.add(product);
    }

    public List<ScreenProduct> getList() {
        return productList;
    }

    public void remove(int index) {
        this.productList.remove(index);
    }

    public ScreenProduct get(int index) {
        return this.productList.get(index);
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0.0);
        total = total.setScale(2, RoundingMode.HALF_UP);
        for (ScreenProduct screenProduct : productList) {
            total = total.add(screenProduct.getPrice());
        }
        return total;
    }

    public void clearList() {
        this.productList.clear();
    }

    public void createSale(User user, Date addDate, boolean cash) throws ServiceException {

        //generate cash register file
        generateCashRegisterFile(cash);

        //create sale
        Sale sale = new Sale();
        sale.setAddDate(addDate);
        sale.setUserId(user.getId());
        sale.setPrice(getTotal());
        try {
            for (ScreenProduct screenProduct : productList) {

                SaleDetail detail = new SaleDetail();
                detail.setPrice(screenProduct.getPrice());
                detail.setQuantity(screenProduct.getQuantity());
                detail.setWeight(screenProduct.getWeight());
                detail.setUnitPrice(screenProduct.getProduct().getPrice());
                detail.setProductId(screenProduct.getProduct().getId());
                detail.setDiscountPrice(screenProduct.getPrice());
                detail.setAddDate(addDate);
                int discount;
                if (screenProduct.getProduct().isOther()) {
                    if (screenProduct.getPrice() == screenProduct.getProduct().getPrice().multiply(new BigDecimal(screenProduct.getQuantity()))) {
                        discount = 0;
                    } else {
                        BigDecimal fullPrice = screenProduct.getProduct().getPrice().multiply(new BigDecimal(screenProduct.getQuantity()));
                        BigDecimal discountPrice = screenProduct.getPrice();
                        discount = fullPrice.subtract(discountPrice).multiply(new BigDecimal(100)).divide(fullPrice).intValue();
                    }
                } else {
                    if (screenProduct.getPrice() == screenProduct.getProduct().getPrice().multiply(new BigDecimal(screenProduct.getQuantity())).multiply(new BigDecimal(screenProduct.getWeight()))) {
                        discount = 0;
                    } else {
                        BigDecimal fullPrice = screenProduct.getProduct().getPrice().multiply(new BigDecimal(screenProduct.getQuantity())).multiply(new BigDecimal(screenProduct.getWeight()));
                        BigDecimal discountPrice = screenProduct.getPrice();
                        discount = fullPrice.subtract(discountPrice).multiply(new BigDecimal(100)).divide(fullPrice, 2, RoundingMode.HALF_UP).intValue();
                    }
                }
                detail.setDiscount(discount);
                sale.addDetail(detail);

                //update inventory
                Product product = screenProduct.getProduct();
                int oldQty = product.getQuantity();
                product.setQuantity(oldQty - screenProduct.getQuantity());
                productDAO.store(product);
                saleDao.addNew(detail);

            }
            saleDao.addNew(sale);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }

        //clear list
        clearList();
    }

    private void generateCashRegisterFile(boolean cash) throws ServiceException {
        try {
            String fileLocation = getProperty(SettingsEnum.CASH_MACHINE_FOLDER);
            String fileName = getProperty(SettingsEnum.CASH_MACHINE_FILE);
            File path = new File(fileLocation);
            if (!path.exists()) {
                path.mkdir();
            }
            File outFile = new File(path + "\\" + fileName);
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            for (ScreenProduct screenProduct : productList) {
                if (screenProduct.getProduct().isGold()) {
                    int priceVal = screenProduct.getProduct().getPrice().intValue();
                    BigDecimal weight = new BigDecimal(screenProduct.getProduct().getWeight());
                    weight = weight.setScale(2, RoundingMode.HALF_UP);
                    out.write(String.format("S,1,______,_,__;%s;%d;%s;0;1;1;0;0;gr", screenProduct.getProduct().getName(), priceVal, weight.toString()));
//                  out.write(String.format("|1             |2 |3 |4 |5|6|7|8|9|10|
                    out.newLine();
                } else {
                    int priceVal = screenProduct.getProduct().getPrice().intValue();
                    int qtyVal = screenProduct.getQuantity();
                    out.write(String.format("S,1,______,_,__;%s;%d;%d;0;1;1;0;0;buc", screenProduct.getProduct().getName(), priceVal, qtyVal));
//                  out.write(String.format("|      1       |2 |3 |4 |5|6|7|8|9|10|
                    out.newLine();
                }
                if (screenProduct.getDiscount() > 0 ) {
                    int discount = screenProduct.getDiscount();
                    out.write(String.format("C,1,______,_,__;1;%d;;;;", discount));
//                  out.write(String.format("|      1       |2|3 |4|5|6|7|8|9|10|
                    out.newLine();
                }
            }
            if (cash) {
                out.write("T,1,______,_,__;0;0;;;;");
//              out.write("|1                 |2|3|4|5|6|7|8|9|10|
            } else {
                out.write("T,1,______,_,__;1;0;;;;");
//              out.write("|1                 |2|3|4|5|6|7|8|9|10|
            }
            out.close();
        } catch (Exception e) {
            throw new ServiceException("There was an error while generating the receipt.");
        }
    }

    private String getProperty(SettingsEnum propertyEnum) throws ServiceException {
        String fileLocation;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(SETTINGS_PROPERTIES)));
            fileLocation = properties.getProperty(propertyEnum.toString());
        } catch (IOException e) {
            throw new ServiceException("Can not read property.");
        }
        return fileLocation;
    }

    public List<SaleDetail> getSaleData(Product product) throws ServiceException {
        try {
            return saleDao.getProductSales(product);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public List<SaleDetail> getSalesFilteredByDate(Date startDate, Date endDate, int currentPage, Category category) throws ServiceException {
        try {
            List<SaleDetail> list = new ArrayList<>();
            if (category != null) {
                list.addAll(saleDao.getPaginatedSalesFilterByDateAndCategory(startDate, endDate, currentPage, category));
            } else {
                list.addAll(saleDao.getPaginatedSalesFilterByDate(startDate, endDate, currentPage));
            }
            return list;
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public int countSalesFilteredByDate(Date startDate, Date endDate, Category category) throws ServiceException {
        try {
            if (category != null) {
                return saleDao.countSalesFilteredByDateAndCategory(startDate, endDate, category);
            } else {
                return saleDao.countSalesFilteredByDate(startDate, endDate);
            }
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
