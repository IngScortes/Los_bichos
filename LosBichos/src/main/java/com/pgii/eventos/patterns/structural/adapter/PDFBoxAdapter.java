package com.pgii.eventos.patterns.structural.adapter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;

/**
 * Adaptador que convierte datos tabulares (List&lt;String[]&gt;) al formato PDF
 * usando la librería Apache PDFBox 2.x.
 *
 * <p>Implementa el patrón Adapter (estructural): expone la interfaz
 * {@link IReporteExporter} que el sistema espera, mientras internamente
 * delega la generación del documento a PDFBox.</p>
 *
 * <p><b>Uso básico:</b></p>
 * <pre>{@code
 *   IReporteExporter exporter = new PDFBoxAdapter();
 *   List<String[]> filas = new ArrayList<>();
 *   filas.add(new String[]{"ID", "Nombre", "Fecha"});   // encabezado
 *   filas.add(new String[]{"1",  "Concierto Rock", "2025-06-01"});
 *   exporter.exportar(filas, "C:/reportes/eventos.pdf");
 * }</pre>
 *
 * @author Los_bichos
 * @version 1.1
 */
public class PDFBoxAdapter implements IReporteExporter {

    // ── Constantes de layout ────────────────────────────────────────────────
    private static final float MARGEN          = 50f;
    private static final float TAMANO_TITULO   = 14f;
    private static final float TAMANO_CUERPO   = 11f;
    private static final float ALTO_LINEA      = 18f;
    private static final float Y_INICIO        = 750f;

    /**
     * Genera un archivo PDF con las filas recibidas y lo guarda en la ruta indicada.
     *
     * <p>La primera fila de {@code filas} se trata como encabezado y se imprime
     * en negrita ({@code PDType1Font.HELVETICA_BOLD}); el resto usa
     * {@code PDType1Font.HELVETICA}.  Si el contenido supera una página se
     * crean páginas adicionales automáticamente.</p>
     *
     * @param filas       Lista de arreglos de cadenas; cada arreglo es una fila
     *                    del reporte y sus elementos son las columnas separadas
     *                    por " | " en el PDF generado.
     * @param rutaArchivo Ruta absoluta o relativa donde se guardará el PDF,
     *                    incluyendo nombre y extensión (ej. {@code "reporte.pdf"}).
     * @throws Exception  Si ocurre un error de E/S al crear o guardar el documento.
     */
    @Override
    public void exportar(List<String[]> filas, String rutaArchivo) throws Exception {
        try (PDDocument doc = new PDDocument()) {

            float y = Y_INICIO;
            PDPage    paginaActual = agregarPagina(doc);
            PDPageContentStream cs = abrirStream(doc, paginaActual);

            for (int i = 0; i < filas.size(); i++) {

                // ── Nueva página si no hay espacio ───────────────────────
                if (y < MARGEN + ALTO_LINEA) {
                    cs.close();
                    paginaActual = agregarPagina(doc);
                    cs           = abrirStream(doc, paginaActual);
                    y            = Y_INICIO;
                }

                // ── Selección de fuente: negrita para encabezado ─────────
                boolean esEncabezado = (i == 0);
                PDType1Font fuente   = esEncabezado
                        ? PDType1Font.HELVETICA_BOLD
                        : PDType1Font.HELVETICA;
                float tamano         = esEncabezado ? TAMANO_TITULO : TAMANO_CUERPO;

                String linea = String.join(" | ", filas.get(i));

                cs.beginText();
                cs.setFont(fuente, tamano);          // ← corrección: fuente obligatoria
                cs.newLineAtOffset(MARGEN, y);
                cs.showText(linea);
                cs.endText();

                y -= ALTO_LINEA;
            }

            cs.close();
            doc.save(rutaArchivo);   // ← guarda el archivo en disco
        }
    }

    // ── Helpers privados ────────────────────────────────────────────────────

    /**
     * Agrega una nueva página de tamaño Letter al documento.
     *
     * @param doc documento destino
     * @return la página recién agregada
     */
    private PDPage agregarPagina(PDDocument doc) {
        PDPage pagina = new PDPage(PDRectangle.LETTER);
        doc.addPage(pagina);
        return pagina;
    }

    /**
     * Abre un {@link PDPageContentStream} en modo APPEND para la página dada.
     *
     * @param doc    documento contenedor
     * @param pagina página sobre la que se escribe
     * @return stream listo para recibir operaciones de texto/gráficos
     * @throws IOException si PDFBox no puede abrir el stream
     */
    private PDPageContentStream abrirStream(PDDocument doc, PDPage pagina) throws IOException {
        return new PDPageContentStream(doc, pagina,
                PDPageContentStream.AppendMode.APPEND, true);
    }
}