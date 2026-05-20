package com.pgii.eventos.patterns.structural.adapter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.util.List;

public class PDFBoxAdapter implements IReporteExporter {
    @Override
    public void exportar(List<String[]> filas, String rutaArchivo) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                //  Línea corregida - esta es la sintaxis correcta para PDFBox 2.x
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);

                float y = 750;
                float margin = 50;
                for (String[] fila : filas) {
                    String linea = String.join(" | ", fila);
                    cs.beginText();
                    cs.newLineAtOffset(margin, y);
                    cs.showText(linea);
                    cs.endText();
                    y -= 20;
                    if (y < 50) break;
                }
            }
            doc.save(rutaArchivo);
        }
    }