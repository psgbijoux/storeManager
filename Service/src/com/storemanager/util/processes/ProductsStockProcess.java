package com.storemanager.util.processes;

import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.models.SaleDetail;
import com.storemanager.service.ProductService;
import com.storemanager.service.SaleService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.ServiceException;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductsStockProcess {

    private static String fileInitial = "stock-init.csv";
    private static String fileUpdated = "stock-update.csv";

    private ProductService productService;
    private SaleService saleService;

    public void startProcess(){

        productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);
        saleService = ServiceLocator.getService(ServiceName.SALE_SERVICE);

        ArrayList<String> barCodes = new ArrayList();
        ArrayList<String> supplyCodes = new ArrayList();

        BufferedReader br;
        BufferedWriter bw;

        int missingItems = 0;

        try {
            bw = new BufferedWriter(new FileWriter(fileUpdated));
            br = new BufferedReader(new FileReader(fileInitial));

            String line;
            StringBuffer sb;

            while ((line = br.readLine()) != null) {

                sb = new StringBuffer(line);
                String[] split = line.split(";");

                try {
                    Product product = productService.getProductByCode(split[0]);
                    if (product != null) {
                        sb.append(";" + product.getPrice());
                        sb.append(";" + product.getQuantity());
                        //get last sale
                        List<SaleDetail> salesList = saleService.getSaleData(product);
                        sb.append(";" + getLastSaleDate(salesList));
                        //get first sale
                        sb.append(";" + getFirstSaleDate(salesList));
                        //get totalSales
                        sb.append(";" + salesList.size());

                        //get last supply
                        List<ProductUpdate> suppliesList = productService.getProductUpdateData(product);
                        sb.append(";" + getLastSupplyDate(suppliesList));
                        //get last supply ammount
                        sb.append(";" + getLastSupplyAmount(suppliesList));
                    } else {
                        sb.append(";lipsa");
                        missingItems++;
                    }
                    //write line
                    bw.write(sb.toString());
                    bw.newLine();
                } catch (ServiceException e) {
                    System.out.print(e.getMessage() + " service except.");
                }
            }
            bw.write(missingItems);
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.print(fileInitial + " file not fount!");
        } catch (IOException e) {
            System.out.print(e.getMessage() + " io except.");
        }
    }

    private String getLastSaleDate(List<SaleDetail> salesList) {
        String formattedDate = "";

        if (salesList.size() > 0) {
            SaleDetail saleDetail = salesList.get(salesList.size() - 1);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
            formattedDate = format.format(saleDetail.getAddDate());
        }
        return formattedDate;
    }

    private String getFirstSaleDate(List<SaleDetail> salesList) {
        String formattedDate = "";

        if (salesList.size() > 0) {
            SaleDetail saleDetail = salesList.get(0);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
            formattedDate = format.format(saleDetail.getAddDate());
        }
        return formattedDate;
    }


    private String getLastSupplyDate(List<ProductUpdate> suppliesList) {
        String formattedDate = "";

        if (suppliesList.size() > 0) {
            ProductUpdate productUpdate = suppliesList.get(suppliesList.size() - 1);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
            formattedDate = format.format(productUpdate.getAddDate());
        }
        return formattedDate;
    }

    private String getLastSupplyAmount(List<ProductUpdate> suppliesList) {
        Integer amount = 0;

        if (suppliesList.size() > 0) {
            ProductUpdate productUpdate = suppliesList.get(suppliesList.size() - 1);
            Date lastSupply = productUpdate.getAddDate();

            for (ProductUpdate supply : suppliesList) {
                if (supply.getAddDate().equals(lastSupply)) {
                    amount += supply.getQuantity();
                }
            }
        }
        return amount.toString();
    }

}
