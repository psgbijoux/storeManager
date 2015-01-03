package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.ZReport;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ZReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);
    private static final Font PLAIN_RED = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL, BaseColor.RED);

    public static void create(String fileName, String reportName, List<ZReport> reportData) throws Exception {
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

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);

        PdfPCell th = new PdfPCell();
        Paragraph thLabel = new Paragraph("Category Name", BOLD);
        th.addElement(thLabel);
        table.addCell(th);
        th = new PdfPCell();
        thLabel = new Paragraph("Type", BOLD);
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

        BigDecimal total = new BigDecimal(0.0);
        for (ZReport zReport : reportData) {
            PdfPCell cell = new PdfPCell();
            Paragraph cellLabel = new Paragraph(zReport.getCategory(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            String type = (zReport.isGold()) ? "Gold" : "Other";
            cell = new PdfPCell();
            cellLabel = new Paragraph(type, PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + zReport.getQuantity(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + zReport.getWeight(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            cell = new PdfPCell();
            cellLabel = new Paragraph("" + zReport.getPrice(), PLAIN);
            cell.addElement(cellLabel);
            table.addCell(cell);
            total = total.add(zReport.getPrice());
        }
        document.add(table);
        addEmptyLine(document, 1);

        table = new PdfPTable(2);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph cellLabel = new Paragraph("Total:", BOLD);
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
