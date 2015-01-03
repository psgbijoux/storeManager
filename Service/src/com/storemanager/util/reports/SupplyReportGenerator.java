package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.SupplyReport;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.SettingsService;
import com.storemanager.util.SettingsEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class SupplyReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);

    public static void create(String fileName, String reportName, List<SupplyReport> reportData) throws Exception {
        Document document = new Document(PageSize.A4);
        document.setMargins(10f, 10f, 10f, 10f);
        PdfWriter.getInstance(document, new FileOutputStream(new File(fileName)));
        document.open();

        //create report
        addEmptyLine(document, 2);

        Paragraph reportNameP = new Paragraph(reportName, BOLD);
        reportNameP.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(reportNameP);

        addEmptyLine(document, 2);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100f);

        PdfPCell th = new PdfPCell();
        Paragraph thLabel = new Paragraph("Product Name", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Bare Code", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Code", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Operation", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Date", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Quantity", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Weight", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Price", BOLD);
        th.addElement(thLabel);
        table.addCell(th);

        int totalQuantity = 0;
        double totalWeight = 0.0;
        BigDecimal totalPrice = new BigDecimal(0.0);

        SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
        BigDecimal goldGR = new BigDecimal(settingsService.loadSettings(SettingsEnum.GOLD_GR_PRICE).getValue());

        for (SupplyReport supplyReport : reportData) {
            PdfPCell cell = new PdfPCell();
            Paragraph cellLabel = new Paragraph(supplyReport.getProductName(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + supplyReport.getBareCode(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + supplyReport.getCode(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + supplyReport.getOperation(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            cellLabel = new Paragraph(df.format(supplyReport.getDate()), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + supplyReport.getQuantity(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + supplyReport.getWeight(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            BigDecimal price = new BigDecimal(0.0);
            if (supplyReport.getWeight() > 0) {
                price = goldGR.multiply(new BigDecimal(supplyReport.getWeight()));
            } else {
                price = supplyReport.getPrice();
            }
            price = price.setScale(2, RoundingMode.HALF_UP);
            cell = new PdfPCell();
            cellLabel = new Paragraph(price.toString(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);

            totalQuantity += supplyReport.getQuantity();
            totalWeight += supplyReport.getWeight();
            totalPrice = totalPrice.add(price);
        }
        document.add(table);

        addEmptyLine(document, 1);

        table = new PdfPTable(2);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph cellLabel = new Paragraph("Total Quantity:", BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph(Integer.toString(totalQuantity), BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_RIGHT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph("Total Weight:", BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph(Double.toString(totalWeight), BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_RIGHT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph("Total Price:", BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph(totalPrice.toString(), BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_RIGHT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        document.add(table);

        document.close();
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
