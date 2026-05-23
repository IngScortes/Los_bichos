package com.pgii.eventos.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una incidencia ocurrida en el contexto de
 * un evento gestionado por la plataforma.
 *
 * <p>Una incidencia puede ser de naturaleza técnica, logística, de seguridad
 * u otro tipo, y sigue un ciclo de vida desde que se reporta hasta que
 * queda cerrada.</p>
 *
 * <h3>Ciclo de vida del estado:</h3>
 * <pre>
 *   ABIERTA → EN_PROCESO → RESUELTA → CERRADA
 *      │                                  ▲
 *      └──── cerrarDirectamente() ────────┘
 * </pre>
 *
 * @author Los_bichos
 * @version 1.0
 */
public class Incidencia {

    // ── Enumeraciones ────────────────────────────────────────────────────────

    /** Categoría de la incidencia. */
    public enum Tipo {
        TECNICA, LOGISTICA, SEGURIDAD, OTRO
    }

    /** Estado dentro del ciclo de vida. */
    public enum Estado {
        ABIERTA, EN_PROCESO, RESUELTA, CERRADA
    }

    /** Nivel de urgencia. */
    public enum Prioridad {
        BAJA, MEDIA, ALTA, CRITICA
    }

    // ── Atributos ────────────────────────────────────────────────────────────

    private String        idIncidencia;
    private String        titulo;
    private String        descripcion;
    private Tipo          tipo;
    private Estado        estado;
    private Prioridad     prioridad;
    private LocalDateTime fechaReporte;
    private LocalDateTime fechaResolucion;
    private Evento evento;
    private String        reportadoPor;
    private String        notasResolucion;

    // ── Constructores ────────────────────────────────────────────────────────

    /** Constructor por defecto. */
    public Incidencia() {
        this.estado       = Estado.ABIERTA;
        this.fechaReporte = LocalDateTime.now();
    }

    /**
     * Constructor principal.
     *
     * @param idIncidencia identificador único
     * @param titulo       título breve (requerido)
     * @param descripcion  descripción detallada
     * @param tipo         categoría (requerido)
     * @param prioridad    nivel de urgencia (requerido)
     * @param evento       evento asociado (requerido)
     * @param reportadoPor nombre de quien reporta (requerido)
     */
    public Incidencia(String idIncidencia, String titulo, String descripcion,
                      Tipo tipo, Prioridad prioridad, com.pgii.eventos.model.Evento evento, String reportadoPor) {
        validarRequerido(idIncidencia, "idIncidencia");
        validarRequerido(titulo,       "titulo");
        validarRequerido(reportadoPor, "reportadoPor");
        Objects.requireNonNull(tipo,      "tipo no puede ser nulo");
        Objects.requireNonNull(prioridad, "prioridad no puede ser nulo");
        Objects.requireNonNull(evento,    "evento no puede ser nulo");

        this.idIncidencia = idIncidencia.trim();
        this.titulo       = titulo.trim();
        this.descripcion  = descripcion;
        this.tipo         = tipo;
        this.prioridad    = prioridad;
        this.evento       = evento;
        this.reportadoPor = reportadoPor.trim();
        this.estado       = Estado.ABIERTA;
        this.fechaReporte = LocalDateTime.now();
    }

    // ── Métodos de negocio ───────────────────────────────────────────────────

    /**
     * Avanza al siguiente estado en el ciclo de vida.
     *
     * @throws IllegalStateException si ya está CERRADA
     */
    public void avanzarEstado() {
        switch (estado) {
            case ABIERTA    -> estado = Estado.EN_PROCESO;
            case EN_PROCESO -> estado = Estado.RESUELTA;
            case RESUELTA   -> {
                estado          = Estado.CERRADA;
                fechaResolucion = LocalDateTime.now();
            }
            case CERRADA    -> throw new IllegalStateException(
                    "La incidencia ya está cerrada.");
        }
    }

    /**
     * Cierra directamente sin seguir el ciclo normal.
     *
     * @param notas justificación del cierre
     * @throws IllegalStateException si ya estaba cerrada
     */
    public void cerrarDirectamente(String notas) {
        if (estado == Estado.CERRADA) {
            throw new IllegalStateException("La incidencia ya está cerrada.");
        }
        this.estado          = Estado.CERRADA;
        this.notasResolucion = notas;
        this.fechaResolucion = LocalDateTime.now();
    }

    /**
     * Indica si la incidencia sigue activa.
     *
     * @return true si está ABIERTA o EN_PROCESO
     */
    public boolean estaActiva() {
        return estado == Estado.ABIERTA || estado == Estado.EN_PROCESO;
    }

    /**
     * Convierte la incidencia a fila para exportar con IReporteExporter.
     *
     * @return String[] con: ID, Título, Tipo, Prioridad, Estado, Fecha, Evento
     */
    public String[] toReporteRow() {
        return new String[]{
                idIncidencia,
                titulo,
                tipo.name(),
                prioridad.name(),
                estado.name(),
                fechaReporte != null ? fechaReporte.toString() : "-",
                evento != null ? evento.getNombre() : "-"
        };
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getIdIncidencia()                        { return idIncidencia; }
    public void   setIdIncidencia(String idIncidencia)     {
        validarRequerido(idIncidencia, "idIncidencia");
        this.idIncidencia = idIncidencia.trim();
    }

    public String getTitulo()                              { return titulo; }
    public void   setTitulo(String titulo)                 {
        validarRequerido(titulo, "titulo");
        this.titulo = titulo.trim();
    }

    public String getDescripcion()                         { return descripcion; }
    public void   setDescripcion(String descripcion)       { this.descripcion = descripcion; }

    public Tipo getTipo()                                  { return tipo; }
    public void setTipo(Tipo tipo)                         { this.tipo = Objects.requireNonNull(tipo); }

    public Estado getEstado()                              { return estado; }

    public Prioridad getPrioridad()                        { return prioridad; }
    public void      setPrioridad(Prioridad prioridad)     { this.prioridad = Objects.requireNonNull(prioridad); }

    public LocalDateTime getFechaReporte()                 { return fechaReporte; }
    public LocalDateTime getFechaResolucion()              { return fechaResolucion; }

    public com.pgii.eventos.model.Evento getEvento()                              { return evento; }
    public void   setEvento(com.pgii.eventos.model.Evento evento)                 { this.evento = Objects.requireNonNull(evento); }

    public String getReportadoPor()                        { return reportadoPor; }
    public void   setReportadoPor(String reportadoPor)     {
        validarRequerido(reportadoPor, "reportadoPor");
        this.reportadoPor = reportadoPor.trim();
    }

    public String getNotasResolucion()                     { return notasResolucion; }
    public void   setNotasResolucion(String notas)         { this.notasResolucion = notas; }

    // ── equals / hashCode / toString ────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Incidencia)) return false;
        Incidencia that = (Incidencia) o;
        return Objects.equals(idIncidencia, that.idIncidencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idIncidencia);
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "id='" + idIncidencia + '\'' +
                ", titulo='" + titulo + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                ", prioridad=" + prioridad +
                '}';
    }

    // ── Helper privado ───────────────────────────────────────────────────────

    private void validarRequerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es requerido.");
        }
    }
}