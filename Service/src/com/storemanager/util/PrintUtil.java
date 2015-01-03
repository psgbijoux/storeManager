package com.storemanager.util;

import com.storemanager.models.Product;

import javax.print.*;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;
import java.math.BigDecimal;

public class PrintUtil {
    public static void printLabel(Product product, int labelCount) {
        for (int i = 0; i < labelCount; i++) {
            printLabel(product);
        }
    }

    public static void printLabel(Product product) {
        try {

            PrintService psZebra = null;
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length; i++) {
                PrintServiceAttribute attr = services[i].getAttribute(PrinterName.class);
                String sPrinterName = ((PrinterName) attr).getValue();
                if (sPrinterName.toLowerCase().indexOf("GC420T".toLowerCase()) >= 0) {
                    psZebra = services[i];
                    break;
                }
            }
            if (psZebra == null) {
                System.out.println("Zebra printer is not found.");
                return;
            }
            DocPrintJob job = psZebra.createPrintJob();

            String zpl = null;
            if (product.isGenerateBareCodeFlag()) {
                if (product.isGold()) {
                    zpl = generateZPL(product.getName(), product.getWeight() + " GR", product.getBareCode());
                } else if (product.isOther()) {
                    zpl = generateZPL(product.getName(), product.getPrice().toString() + " LEI", product.getBareCode());
                }
            } else {
                BigDecimal price = product.getPrice();
                zpl = generateZPLNoBareCode(product.getName(), product.getCode(), price.toString() + " LEI");
            }
            byte[] by = zpl.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(by, flavor, null);
            job.print(doc, null);

        } catch (PrintException e) {
            e.printStackTrace();
        }
    }


    private static String generateZPL(String nameLabel, String priceLabel, String bareCode) {
        StringBuilder builder = new StringBuilder();
        builder.append("^XA\n");
        builder.append("^FT120,40,0");
        builder.append("^A0,32,25");
        builder.append("^FD").append(nameLabel).append("^FS\n");
        builder.append("^FT120,90,0");
        builder.append("^A0,32,25");
        builder.append("^FD").append(priceLabel).append("^FS\n");
        builder.append("^FO290,12^BY2.2\n");
        builder.append("^B8N,55,Y,N\n");
        builder.append("^FD").append(bareCode).append("^FS\n");
        builder.append("^XZ");
        return builder.toString();
    }

    private static String generateZPLNoBareCode(String nameLabel, String description, String priceLabel) {
        StringBuilder builder = new StringBuilder();
        builder.append("^XA\n");
        builder.append("~SD30");
        builder.append("^FT120,50,0");
        builder.append("^A0,32,25").append("^FD").append(nameLabel).append("^FS\n");
        builder.append("^FT140,90,0");
        builder.append("^A0,32,25");
        builder.append("^FD").append(description).append("^FS\n");
        builder.append("^FO290,45^BY2.2\n");
        builder.append("^A0,50,35");
        builder.append("^FD").append(priceLabel).append("^FS\n");
        builder.append("^XZ");

        return builder.toString();
    }

    private static String generateZPL(String nameLabel, String priceLabel) {
        return "";
    }

}
