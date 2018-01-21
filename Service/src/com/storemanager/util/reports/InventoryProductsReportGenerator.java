package com.storemanager.util.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemanager.models.StockReport;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InventoryProductsReportGenerator {
    private static final Font BOLD = new Font(Font.FontFamily.UNDEFINED, 9, Font.BOLDITALIC);
    private static final Font BIGPLAIN = new Font(Font.FontFamily.UNDEFINED, 7, Font.NORMAL);
    private static final Font PLAIN = new Font(Font.FontFamily.UNDEFINED, 5, Font.NORMAL);


    public static void create(String fileName, String reportName, List<String[]> reportData) throws Exception {
        Document document = new Document(PageSize.A4.rotate());

        document.setMargins(10f, 10f, 10f, 10f);
        PdfWriter.getInstance(document, new FileOutputStream(new File(fileName)));
        document.open();

        PdfPTable table;

        for(int i=0; i<=(reportData.size()/30); i++) {

            Paragraph reportNameP = new Paragraph("LISTA DE INVENTARIERE", BOLD);
            reportNameP.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(reportNameP);

            addEmptyLine(document, 1);

            table = new PdfPTable(27);
            table.setWidthPercentage(100f);

            createHeader(table, String.valueOf(i+1));
            if (i == (reportData.size()/30)) {
                for(int j=0; j<reportData.size()%30 ; j++) {
                    createContentRow(table, reportData.get(i*30+j), i*30+j+1);

                }
            } else {
                for (int j=0; j<30; j++) {
                    createContentRow(table, reportData.get(i*30+j), i*30+j+1);
                }
            }
            createFooter(table);

            document.add(table);
        }
        document.close();
    }

    private static void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private static void createHeader(PdfPTable table, String page) {

        table.addCell(createCell(7, 1, BOLD, 0, "Unitatea S.C. P.S.G. COM S.R.L."));
        table.addCell(createCell(12,2, BOLD, 1, "LISTA DE INVENTARIERE\nDATA 31.12.2017"));
        table.addCell(createCell(6, 1, BOLD, 0, "Gestiunea 1"));
        table.addCell(createCell(2, 1, BOLD, 1, "Pagina"));
        table.addCell(createCell(7, 1, BOLD, 0, "Magazin Memorandumului 12"));
        table.addCell(createCell(6, 1, BOLD, 0, "Loc de depozitare: magazin"));
        table.addCell(createCell(2, 1, BOLD, 1, page));
        table.addCell(createCell(1, 3, BOLD, 1, "Nr.\ncrt."));
        table.addCell(createCell(6, 3, BOLD, 1, "DENUMIREA\nBUNURILOR\nINVENTARIATE"));
        table.addCell(createCell(3, 3, BOLD, 1, "COD\nsau\nNR.INV."));
        table.addCell(createCell(1, 3, BOLD, 1, "U.M."));
        table.addCell(createCell(8, 1, BOLD, 1, "CANTITATI"));
        table.addCell(createCell(6, 1, BOLD, 1, "VALORI"));
        table.addCell(createCell(2, 3, BOLD, 1, "Alte\nmentiuni"));
        table.addCell(createCell(4, 1, BOLD, 1, "Stocuri"));
        table.addCell(createCell(4, 1, BOLD, 1, "Diferente"));
        table.addCell(createCell(2, 2, BOLD, 1, "Pret unitar"));
        table.addCell(createCell(4, 1, BOLD, 1, "Diferente"));
        table.addCell(createCell(2, 1, BOLD, 1, "scriptice"));
        table.addCell(createCell(2, 1, BOLD, 1, "faptice"));
        table.addCell(createCell(2, 1, BOLD, 1, "plus"));
        table.addCell(createCell(2, 1, BOLD, 1, "minus"));
        table.addCell(createCell(2, 1, BOLD, 1, "plus"));
        table.addCell(createCell(2, 1, BOLD, 1, "minus"));
    }

    private static void createContentRow(PdfPTable table, String[] stockReport, int index) {



        table.addCell(createCell(1, 1, PLAIN, 0, Integer.toString(index)));
        table.addCell(createCell(6, 1, PLAIN, 0, stockReport[0]));
        table.addCell(createCell(3, 1, PLAIN, 0, stockReport[1]));
        table.addCell(createCell(1, 1, PLAIN, 0, "buc."));
        table.addCell(createCell(2, 1, BIGPLAIN, 2, stockReport[2]));
        table.addCell(createCell(2, 1, PLAIN, 0, ""));
        table.addCell(createCell(2, 1, PLAIN, 0, ""));
        table.addCell(createCell(2, 1, PLAIN, 0, ""));
        table.addCell(createCell(2, 1, PLAIN, 2, stockReport[3]));
        table.addCell(createCell(2, 1, PLAIN, 0, ""));
        table.addCell(createCell(2, 1, PLAIN, 0, ""));
        table.addCell(createCell(2, 1, PLAIN, 2, stockReport[4]));
    }

    private static void createFooter(PdfPTable table) {

        table.addCell(createCell(7, 3, BOLD, 0, "Numele si prenumele:"));
        table.addCell(createCell(8, 1, BOLD, 1, "Comisia de inventariere"));
        table.addCell(createCell(6, 1, BOLD, 1, "Gestionar"));
        table.addCell(createCell(6, 1, BOLD, 1, "Contabilitate"));
        table.addCell(createCell(3, 2, BOLD, 1, " \n "));
        table.addCell(createCell(3, 2, BOLD, 1, " \n "));
        table.addCell(createCell(2, 2, BOLD, 1, " \n "));
        table.addCell(createCell(6, 2, BOLD, 1, " \n "));
        table.addCell(createCell(6, 2, BOLD, 1, " \n "));
        table.addCell(createCell(7, 1, BOLD, 0, "Semnatura"));
        table.addCell(createCell(3, 1, BOLD, 1, " "));
        table.addCell(createCell(3, 1, BOLD, 1, " "));
        table.addCell(createCell(2, 1, BOLD, 1, " "));
        table.addCell(createCell(6, 1, BOLD, 1, " "));
        table.addCell(createCell(6, 1, BOLD, 1, " "));
    }

    /* @param int align: 0=left; 1=center; 2=right*/
    private static PdfPCell createCell(int colSpan, int rowSpan, Font font, int align, String content) {

        Paragraph cellLabel = new Paragraph(content, font);
        PdfPCell cell = new PdfPCell(cellLabel);
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        cell.setHorizontalAlignment(align);
        cell.setFixedHeight(14);
        return cell;
    }
}
