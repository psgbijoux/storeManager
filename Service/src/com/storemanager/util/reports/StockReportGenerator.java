package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.StockReport;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.SettingsService;
import com.storemanager.util.SettingsEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StockReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);

    public static void create(String fileName, String reportName, List<StockReport> reportData) throws Exception {
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

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);

        PdfPCell th = new PdfPCell();
        Paragraph thLabel = new Paragraph("Category Name", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("# of Products", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Weight", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Gold", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Other", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Price", BOLD);
        th.addElement(thLabel);
        table.addCell(th);

        SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
        BigDecimal goldGR = new BigDecimal(settingsService.loadSettings(SettingsEnum.GOLD_GR_PRICE).getValue());

        BigDecimal total = new BigDecimal(0.00);
        for (StockReport stockReport : reportData) {
            PdfPCell cell = new PdfPCell();
            Paragraph cellLabel = new Paragraph(stockReport.getCategoryName(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + stockReport.getItems(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + stockReport.getWeight(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + stockReport.isGold(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + stockReport.isOther(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            BigDecimal price = stockReport.getPrice();
            if (stockReport.isOther()) {
                price = price.setScale(2, RoundingMode.HALF_UP);
                total = total.add(price);
            } else {
                if (stockReport.getItems() > 0)
                    price = new BigDecimal(stockReport.getWeight()).multiply(goldGR);
                else
                    price = new BigDecimal(0.0);
                price = price.setScale(2, RoundingMode.HALF_UP);
                total = total.add(price);
            }
            cellLabel = new Paragraph("" + price, PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
        }
        document.add(table);

        addEmptyLine(document, 1);

        table = new PdfPTable(2);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph cellLabel = new Paragraph("Total stock:", BOLD);
        cellLabel.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(cellLabel);
        table.addCell(cell);

        total = total.setScale(2, RoundingMode.HALF_UP);
        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cellLabel = new Paragraph(total.toString(), BOLD);
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
