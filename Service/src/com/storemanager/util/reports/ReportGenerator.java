package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.ReportData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class ReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);

    public static void create(String fileName, String reportName, ReportData reportData) throws Exception {
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

        PdfPTable table = new PdfPTable(reportData.getHeader().length + 3);
        table.setWidthPercentage(100f);

        for (int i = 0; i < reportData.getHeader().length; i++) {
            PdfPCell cell = new PdfPCell();
            Paragraph labelP = new Paragraph(reportData.getHeader()[i], BOLD);
            cell.addElement(labelP);
            if (i <= 2) {
                cell.setColspan(2);
            }
            table.addCell(cell);
        }

        for (String[] dataRow : reportData.getData()) {
            for (int i = 0; i < dataRow.length; i++) {
                PdfPCell cell = new PdfPCell();
                Paragraph labelP = new Paragraph(dataRow[i], PLAIN);
                cell.addElement(labelP);
                if (i <= 2) {
                    cell.setColspan(2);
                }
                table.addCell(cell);
            }
        }
        document.add(table);

        PdfPTable footer = new PdfPTable(2);
        table.setWidthPercentage(100f);

        for (Map.Entry<String, String> entry : reportData.getFooter().entrySet()) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            Paragraph labelP = new Paragraph(entry.getKey(), BOLD);
            labelP.setAlignment(Paragraph.ALIGN_LEFT);
            cell.addElement(labelP);
            footer.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            Paragraph val = new Paragraph(entry.getValue(), BOLD);
            val.setAlignment(Paragraph.ALIGN_RIGHT);
            cell.addElement(val);
            footer.addCell(cell);
        }
        document.add(footer);

        document.close();
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
