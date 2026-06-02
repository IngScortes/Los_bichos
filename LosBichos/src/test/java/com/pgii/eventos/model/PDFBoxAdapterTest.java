package com.pgii.eventos.model;

import com.pgii.eventos.patterns.structural.adapter.IReporteExporter;
import com.pgii.eventos.patterns.structural.adapter.PDFBoxAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para {@link PDFBoxAdapter}.
 *
 * <p>Verifica que el adaptador genere efectivamente un archivo PDF en disco
 * con contenido no vacío, y que maneje correctamente los casos borde
 * (lista vacía, ruta inválida, muchas filas).</p>
 *
 * <p>Usa {@code @TempDir} de JUnit 5 para trabajar en un directorio temporal
 * que se limpia automáticamente al finalizar cada prueba.</p>
 *
 * @author Los_bichos
 */
@DisplayName("PDFBoxAdapter — pruebas de integración")
class PDFBoxAdapterTest {

    @TempDir
    Path directorioTemp;

    private IReporteExporter adapter;
    private List<String[]> filasBase;

    @BeforeEach
    void setUp() {
        adapter = new PDFBoxAdapter();

        filasBase = new ArrayList<>();
        // Fila 0 = encabezado
        filasBase.add(new String[]{"ID", "Título", "Tipo", "Prioridad", "Estado", "Fecha", "Evento"});
        filasBase.add(new String[]{"1", "Fallo de sonido", "TECNICA", "ALTA", "ABIERTA", "2025-06-01T10:00", "Concierto"});
        filasBase.add(new String[]{"2", "Sin acceso VIP", "LOGISTICA", "MEDIA", "EN_PROCESO", "2025-06-01T11:30", "Concierto"});
    }

    // ── Generación básica ────────────────────────────────────────────────────

    @Test
    @DisplayName("Genera un archivo PDF que existe en disco")
    void generaArchivoEnDisco() throws Exception {
        String ruta = directorioTemp.resolve("reporte.pdf").toString();
        adapter.exportar(filasBase, ruta);

        File archivo = new File(ruta);
        assertTrue(archivo.exists(), "El archivo PDF debe existir en disco");
    }

    @Test
    @DisplayName("El archivo PDF generado no está vacío")
    void archivoNoEstaVacio() throws Exception {
        String ruta = directorioTemp.resolve("reporte.pdf").toString();
        adapter.exportar(filasBase, ruta);

        File archivo = new File(ruta);
        assertTrue(archivo.length() > 0, "El archivo PDF no debe estar vacío");
    }

    @Test
    @DisplayName("El archivo tiene encabezado PDF válido (%PDF-)")
    void archivoTieneEncabezadoPDF() throws Exception {
        String ruta = directorioTemp.resolve("reporte.pdf").toString();
        adapter.exportar(filasBase, ruta);

        byte[] bytes = java.nio.file.Files.readAllBytes(Path.of(ruta));
        String inicio = new String(bytes, 0, Math.min(5, bytes.length));
        assertEquals("%PDF-", inicio, "El archivo debe comenzar con el magic number de PDF");
    }

    // ── Casos borde ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Lista vacía genera PDF sin errores (documento en blanco)")
    void listaVaciaGeneraPDFVacio() {
        String ruta = directorioTemp.resolve("vacio.pdf").toString();
        assertDoesNotThrow(() -> adapter.exportar(new ArrayList<>(), ruta));
        assertTrue(new File(ruta).exists());
    }

    @Test
    @DisplayName("Solo encabezado (1 fila) genera PDF válido")
    void soloEncabezadoGeneraPDF() throws Exception {
        List<String[]> soloHeader = new ArrayList<>();
        soloHeader.add(new String[]{"ID", "Título", "Tipo", "Estado"});
        String ruta = directorioTemp.resolve("header.pdf").toString();
        adapter.exportar(soloHeader, ruta);
        assertTrue(new File(ruta).length() > 0);
    }

    @Test
    @DisplayName("Muchas filas (multi-página) no lanza excepción")
    void muchasFilasGeneraPDFMultipagina() {
        List<String[]> muchasFilas = new ArrayList<>();
        muchasFilas.add(new String[]{"ID", "Descripción"});
        for (int i = 1; i <= 200; i++) {
            muchasFilas.add(new String[]{String.valueOf(i), "Incidencia número " + i});
        }
        String ruta = directorioTemp.resolve("multipagina.pdf").toString();
        assertDoesNotThrow(() -> adapter.exportar(muchasFilas, ruta));
        assertTrue(new File(ruta).exists());
    }

    @Test
    @DisplayName("Ruta inválida (directorio inexistente) lanza excepción")
    void rutaInvalidaLanzaExcepcion() {
        String rutaMala = "/ruta/que/no/existe/reporte.pdf";
        assertThrows(Exception.class, () -> adapter.exportar(filasBase, rutaMala));
    }

    // ── Integración con Incidencia.toReporteRow() ────────────────────────────

    @Test
    @DisplayName("Exportar filas generadas desde Incidencia.toReporteRow() funciona")
    void exportarFilasDeIncidencia() throws Exception {
        Evento evento = new Evento(
                "EVT-002",
                "Festival Jazz",
                CategoriaEvento.CONCIERTO,
                "Festival de jazz en vivo",
                "Medellín",
                LocalDateTime.now().plusDays(5),
                new Recinto("Rec-002", "Plaza Mayor", "Calle 10", "Medellín")
        );
        Incidencia inc = new Incidencia(
                "INC-010",
                "Corte de luz",
                "Se fue la energía en el escenario principal.",
                Incidencia.Tipo.TECNICA,
                Incidencia.Prioridad.CRITICA,
                evento,
                "Supervisor");

        List<String[]> filas = new ArrayList<>();
        filas.add(new String[]{"ID", "Título", "Tipo", "Prioridad", "Estado", "Fecha", "Evento"});
        filas.add(inc.toReporteRow());

        String ruta = directorioTemp.resolve("incidencias.pdf").toString();
        adapter.exportar(filas, ruta);

        assertTrue(new File(ruta).exists());
        assertTrue(new File(ruta).length() > 0);
    }
}