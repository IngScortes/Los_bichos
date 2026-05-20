package com.pgii.eventos.patterns.structural.adapter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.util.List;

public class PDFBoxAdapter implements IReporteExporter {
    @Override
    public void exportar(List<String[]> filas, String rutaArchivo) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                //  Usar fuente por defecto (sin setFont explícito)

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
}