package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.InventoryReport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class InventoryReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);
    private static final Font PLAIN_RED = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL, BaseColor.RED);

    public static void create(String fileName, String reportName, List<InventoryReport> reportData) throws Exception {
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
        thLabel = new Paragraph("Quantity", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Stock", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Difference", BOLD);
        th.addElement(thLabel);
        table.addCell(th);


        for (InventoryReport inventoryReport : reportData) {
            PdfPCell cell = new PdfPCell();
            Paragraph cellLabel = new Paragraph(inventoryReport.getName(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph(inventoryReport.getBareCode(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph(inventoryReport.getCode(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + inventoryReport.getQuantity(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + inventoryReport.getStock(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + (inventoryReport.getQuantity() - inventoryReport.getStock()), PLAIN_RED);
            cell.addElement(cellLabel);
            table.addCell(cell);
        }
        document.add(table);
        document.close();
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
