package com.pgii.eventos.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad {@link Incidencia}.
 *
 * @author Los_bichos
 */
@DisplayName("Incidencia — pruebas unitarias")
class IncidenciaTest {

    private Evento    eventoBase;
    private Incidencia incidencia;

    @BeforeEach
    void setUp() {
        eventoBase = new Evento(
                "EVT-001",
                "Concierto Rock",
                CategoriaEvento.CONCIERTO,
                "Gran concierto de rock",
                "Bogotá",
                LocalDateTime.now().plusDays(10),
                new Recinto("Rec-001", "Auditorio Nacional", "Calle 1", "Bogotá")
        );

        incidencia = new Incidencia(
                "INC-001",
                "Fallo de sonido",
                "El sistema de audio dejó de funcionar.",
                Incidencia.Tipo.TECNICA,
                Incidencia.Prioridad.ALTA,
                eventoBase,
                "Carlos Ruiz"
        );
    }

    // ── Constructor y valores iniciales ──────────────────────────────────────

    @Test
    @DisplayName("Estado inicial debe ser ABIERTA")
    void estadoInicialAbierta() {
        assertEquals(Incidencia.Estado.ABIERTA, incidencia.getEstado());
    }

    @Test
    @DisplayName("Fecha de reporte se asigna automáticamente")
    void fechaReporteNoNula() {
        assertNotNull(incidencia.getFechaReporte());
    }

    @Test
    @DisplayName("Fecha de resolución es nula al crearse")
    void fechaResolucionNulaInicial() {
        assertNull(incidencia.getFechaResolucion());
    }

    // ── Validaciones ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Título vacío lanza IllegalArgumentException")
    void tituloBlancoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                new Incidencia("INC-002", "  ", "desc",
                        Incidencia.Tipo.OTRO, Incidencia.Prioridad.BAJA,
                        eventoBase, "Ana")
        );
    }

    @Test
    @DisplayName("Evento nulo lanza NullPointerException")
    void eventoNuloLanzaExcepcion() {
        assertThrows(NullPointerException.class, () ->
                new Incidencia("INC-002", "Titulo", "desc",
                        Incidencia.Tipo.OTRO, Incidencia.Prioridad.BAJA,
                        null, "Ana")
        );
    }

    // ── Ciclo de vida ────────────────────────────────────────────────────────

    @Test
    @DisplayName("avanzarEstado: ABIERTA → EN_PROCESO")
    void avanzarDeAbiertaAEnProceso() {
        incidencia.avanzarEstado();
        assertEquals(Incidencia.Estado.EN_PROCESO, incidencia.getEstado());
    }

    @Test
    @DisplayName("avanzarEstado: EN_PROCESO → RESUELTA")
    void avanzarDeEnProcesoAResuelta() {
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        assertEquals(Incidencia.Estado.RESUELTA, incidencia.getEstado());
    }

    @Test
    @DisplayName("avanzarEstado: RESUELTA → CERRADA y asigna fecha de resolución")
    void avanzarDeResueltaACerrada() {
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        assertEquals(Incidencia.Estado.CERRADA, incidencia.getEstado());
        assertNotNull(incidencia.getFechaResolucion());
    }

    @Test
    @DisplayName("avanzarEstado en CERRADA lanza IllegalStateException")
    void avanzarDesdeCerradaLanzaExcepcion() {
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        assertThrows(IllegalStateException.class, incidencia::avanzarEstado);
    }

    // ── Cierre directo ───────────────────────────────────────────────────────

    @Test
    @DisplayName("cerrarDirectamente cierra desde cualquier estado")
    void cerrarDirectamenteDesdeAbierta() {
        incidencia.cerrarDirectamente("Duplicado");
        assertEquals(Incidencia.Estado.CERRADA, incidencia.getEstado());
        assertNotNull(incidencia.getFechaResolucion());
        assertEquals("Duplicado", incidencia.getNotasResolucion());
    }

    @Test
    @DisplayName("cerrarDirectamente en CERRADA lanza IllegalStateException")
    void cerrarDirectamenteYaCerradaLanzaExcepcion() {
        incidencia.cerrarDirectamente("nota");
        assertThrows(IllegalStateException.class, () ->
                incidencia.cerrarDirectamente("otra nota"));
    }

    // ── estaActiva ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("estaActiva es true para ABIERTA y EN_PROCESO")
    void estaActivaVerdaderaEnEstadosActivos() {
        assertTrue(incidencia.estaActiva());
        incidencia.avanzarEstado();
        assertTrue(incidencia.estaActiva());
    }

    @Test
    @DisplayName("estaActiva es false para RESUELTA y CERRADA")
    void estaActivaFalsaEnEstadosTerminales() {
        incidencia.avanzarEstado();
        incidencia.avanzarEstado();
        assertFalse(incidencia.estaActiva());
        incidencia.avanzarEstado();
        assertFalse(incidencia.estaActiva());
    }

    // ── toReporteRow ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("toReporteRow devuelve 7 columnas con datos correctos")
    void toReporteRowFormatoValido() {
        String[] fila = incidencia.toReporteRow();
        assertEquals(7, fila.length);
        assertEquals("INC-001",        fila[0]);
        assertEquals("Fallo de sonido", fila[1]);
        assertEquals("TECNICA",         fila[2]);
        assertEquals("ALTA",            fila[3]);
        assertEquals("ABIERTA",         fila[4]);
        assertEquals("Concierto Rock",  fila[6]);
    }
}