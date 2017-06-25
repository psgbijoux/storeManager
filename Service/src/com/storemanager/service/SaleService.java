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
    private SaleDao saleDao;
    private ProductDAO productDAO;
    private List<ScreenProduct> productList;
    private final String CASH_REGISTER_OUT_FILE = "cashRegisterOut.txt";
    private final String CASH_REGISTER_OUT_PATH = "Cash_Register\\";

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

    public void createTipsPrint(String tipsStr) throws ServiceException {

        try {
            Double tipsDouble = Double.parseDouble(tipsStr);

            generateCashRegisterFileForTips(tipsDouble);
            printReceipt();

        } catch (NumberFormatException ex) {
            throw new ServiceException("Valoare incorecta pentru bacsis!");
        }
    }

    public void createSale(User user, Date addDate, boolean cash) throws ServiceException {

        //generate cash register file
        if(cash == true) {
            generateCashRegisterFile();
        } else {
            generateCashRegisterFileCard();
        }
        //send cash register command
        printReceipt();

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

    private void generateCashRegisterFile() throws ServiceException {
        try {
            File path = new File(CASH_REGISTER_OUT_PATH);
            if (!path.exists()) {
                path.mkdir();
            }
            File outFile = new File(path + "\\" + CASH_REGISTER_OUT_FILE);
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            for (ScreenProduct screenProduct : productList) {
                if (screenProduct.getProduct().isGold()) {
                    int priceVal = screenProduct.getProduct().getPrice().multiply(new BigDecimal(100)).intValue();
                    BigDecimal weight = new BigDecimal(screenProduct.getProduct().getWeight());
                    weight = weight.multiply(new BigDecimal(1000));
                    weight = weight.setScale(0, RoundingMode.HALF_UP);
                    out.write(String.format("1;%s;3;5;1;%d;%s;0", screenProduct.getProduct().getName(), priceVal, weight.toString()));
                    out.newLine();
                } else {
                    int priceVal = screenProduct.getProduct().getPrice().multiply(new BigDecimal(100)).intValue();
                    int qtyVal = screenProduct.getQuantity() * 1000;
                    out.write(String.format("1;%s;3;5;1;%d;%d;0", screenProduct.getProduct().getName(), priceVal, qtyVal));
                    out.newLine();
                }
                if (screenProduct.getDiscount() > 0 ) {
                    int discount = screenProduct.getDiscount();
                    out.write(String.format("7;1;0;1;%d", discount));
                    out.newLine();
                }
            }
            out.write("3");
            out.close();
        } catch (Exception e) {
            throw new ServiceException("There was an error while generating the receipt.");
        }
    }

    private void generateCashRegisterFileForTips(Double tips) throws ServiceException {

        tips = tips*100;

        try {
            File path = new File(CASH_REGISTER_OUT_PATH);
            if (!path.exists()) {
                path.mkdir();
            }
            File outFile = new File(path + "\\" + CASH_REGISTER_OUT_FILE);
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);

            out.write(String.format("1;Bacsis;3;5;2;%d;%d;0", tips.intValue(), 1000));
            out.newLine();

            out.write("3");
            out.close();
        } catch (Exception e) {
            throw new ServiceException("There was an error while generating the receipt.");
        }
    }

    private void generateCashRegisterFileCard() throws ServiceException {
        try {
            File path = new File(CASH_REGISTER_OUT_PATH);
            if (!path.exists()) {
                path.mkdir();
            }
            File outFile = new File(path + "\\" + CASH_REGISTER_OUT_FILE);
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            for (ScreenProduct screenProduct : productList) {
                if (screenProduct.getProduct().isGold()) {
                    int priceVal = screenProduct.getProduct().getPrice().multiply(new BigDecimal(100)).intValue();
                    BigDecimal weight = new BigDecimal(screenProduct.getProduct().getWeight());
                    weight = weight.multiply(new BigDecimal(1000));
                    weight = weight.setScale(0, RoundingMode.HALF_UP);
                    out.write(String.format("1;%s;1;1;1;%d;%s;0", screenProduct.getProduct().getName(), priceVal, weight.toString()));
                    out.newLine();
                } else {
                    int priceVal = screenProduct.getProduct().getPrice().multiply(new BigDecimal(100)).intValue();
                    int qtyVal = screenProduct.getQuantity() * 1000;
                    out.write(String.format("1;%s;1;1;1;%d;%d;0", screenProduct.getProduct().getName(), priceVal, qtyVal));
                    out.newLine();
                }
                if (screenProduct.getDiscount() > 0 ) {
                    int discount = screenProduct.getDiscount();
                    out.write(String.format("7;1;0;1;%d", discount));
                    out.newLine();
                }
            }
            int total = (getTotal().multiply(new BigDecimal(100))).intValue();
            out.write(String.format("5;%d;2", total));
            //out.write("3");
            out.close();
        } catch (Exception e) {
            throw  new ServiceException("There was an error while generating the receipt.");
        }
    }

    private void printReceipt() throws ServiceException {
        String serialPort = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("settings.properties")));
            serialPort = properties.getProperty(SettingsEnum.SERIAL_PORT.toString());
        } catch (IOException e) {
            throw new ServiceException("Can not read serial port setting.");
        }
        try {
            String[] commands = {CASH_REGISTER_OUT_PATH + "Comm2A.exe", "/data_file:" + CASH_REGISTER_OUT_PATH + "cashRegisterOut.txt", "/t", "/ro", "/" + serialPort, "/speed:38400", "/ecr:13", "/num:1", "/o:18", "/err_file:error.err"};

            Process p = Runtime.getRuntime().exec(commands);
            p.waitFor();
            File file = new File(CASH_REGISTER_OUT_PATH + "cashRegisterOut.txt");
//            file.delete();
        } catch (Exception e) {
            throw new ServiceException("There was an error while printing the receipt.");
        }
    }

    public List<SaleDetail> getSaleData(Product product) throws ServiceException{
        try {
            List<SaleDetail> list = saleDao.getProductSales(product);
            return list;
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
