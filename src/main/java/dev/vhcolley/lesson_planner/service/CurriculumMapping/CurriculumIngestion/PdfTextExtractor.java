// References:
// - Apache PDFBox for PDF text extraction
//https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/text/PDFTextStripper.html

package dev.vhcolley.lesson_planner.service.CurriculumMapping.CurriculumIngestion;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class PdfTextExtractor {

    public String extractAllText(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            return pdfStripper.getText(document);
        }
    }
    
}
