package com.storemanager.util;

import com.storemanager.models.Product;

import javax.print.*;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;
import java.math.BigDecimal;

public class PrintUtil {

    public static void printLabel(Product product, int labelCount) {
        for (int i = 0; i < labelCount; i++) {
            printLabel(PrintLabelEnum.PRODUCT, product, null, null, null, null);
        }
    }

    public static void printPriceLabel(String priceLabel, int labelCount) {
        for (int i = 0; i < labelCount; i++) {
            printLabel(PrintLabelEnum.PRICE, null, null, null, priceLabel, null);
        }
    }

    public static void printWeightLabel(String weightLabel, String textLabel1, String textLabel2, int labelCount) {
        for (int i = 0; i < labelCount; i++) {
            printLabel(PrintLabelEnum.WEIGHT, null, textLabel1, textLabel2, null, weightLabel);
        }
    }

    public static void printLabel(PrintLabelEnum printLabelEnum, Product product, String textLabel1,
                                  String textLabel2, String priceLabel, String weightLabel) {
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
            switch (printLabelEnum) {
                case PRICE:
                    zpl = generatePriceZPL(priceLabel);
                    break;
                case PRODUCT:
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
                    break;
                case WEIGHT:
                    zpl = generateWeightZPL(weightLabel, textLabel1, textLabel2);
                    break;
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
        builder.append("^FT110,90,0");
        builder.append("^A0,25,20");
        builder.append("^FD").append(description).append("^FS\n");
        builder.append("^FO300,45^BY2.2\n");
        builder.append("^A0,50,35");
        builder.append("^FD").append(priceLabel).append("^FS\n");
        builder.append("^XZ");
        return builder.toString();
    }

    private static String generatePriceZPL(String priceLabel) {
        StringBuilder builder = new StringBuilder();
        builder.append("^XA\n");
        builder.append("~SD30");
        builder.append("^FO120,30,0");
        builder.append("^A0,50,35");
        builder.append("^FD").append(priceLabel).append("^FS\n");
        builder.append("^FO300,30^BY1.0\n");
        builder.append("^A0,50,35");
        builder.append("^FD").append(priceLabel).append("^FS\n");
        builder.append("^XZ");
        return builder.toString();
    }

    private static String generateWeightZPL(String weightLabel, String textLabel1, String textLabel2) {
        StringBuilder builder = new StringBuilder();
        builder.append("^XA\n");
        builder.append("~SD30");
        builder.append("^FO120,30,0");
        builder.append("^A0,30,25");
        builder.append("^FD").append(textLabel1).append("^FS\n");
        builder.append("^FO140,70,0");
        builder.append("^A0,30,25");
        builder.append("^FD").append(textLabel2).append("^FS\n");
        builder.append("^FO290,30^BY1.0\n");
        builder.append("^A0,64,50");
        builder.append("^FD").append(weightLabel).append("^FS\n");
        builder.append("^XZ");
        return builder.toString();
    }

}
