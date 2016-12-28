package com.storemanager.util.processes;

import com.storemanager.service.ProductService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.ServiceException;
import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductsUpdateProcess {

    private static String file = "stock.csv";

    private ProductService productService;

    public void startProcess(){

        productService = ServiceLocator.getService(ServiceName.PRODUCT_SERVICE);

        ArrayList<String> barCodes = new ArrayList();
        ArrayList<String> supplyCodes = new ArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                if (split[1] != null && split[2] != null) {
                    supplyCodes.add(split[1]);
                    barCodes.add(split[2]);
                }
            }
            productService.updateProductsDescription(barCodes, supplyCodes);
        } catch (FileNotFoundException e) {
            System.out.print(file + " file not fount!");
        } catch (IOException e) {
            System.out.print(e.getMessage() + " io except.");
        }
    }

}
