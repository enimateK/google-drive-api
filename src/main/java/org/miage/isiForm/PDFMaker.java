package org.miage.isiForm;

import org.miage.isiForm.google.sheets.Cell;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;

public class PDFMaker {
    /*public static void createPDFFromList (List<List<List<Cell>>> fileContent) throws IOException {

        PDDocument document = new PDDocument();

        for(List<List<Cell>> sheet : fileContent) {
            int i = 0;
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.TIMES_ROMAN, 12);
            content.newLineAtOffset(100, 700);
            while (i < sheet.size()) {

                for (Cell cell : sheet.get(i)) {
                    content.newLineAtOffset(0, -15);
                    String text = cell.getValue().replace("\n", " ").replace("\r", " ");
                    content.showText(text);
                }
                i++;
            }
            content.endText();
            content.close();
        }



        document.save("./my_doc.pdf");

        System.out.println("PDF created");

        document.close();
    }*/
}
