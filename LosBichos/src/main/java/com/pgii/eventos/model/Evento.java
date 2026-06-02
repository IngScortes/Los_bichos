package com.pgii.eventos.model;

import com.pgii.eventos.patterns.behavioral.observer.Observador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private String idEvento;
    private String nombre;
    private CategoriaEvento categoria;
    private String descripcion;
    private String ciudad;
    private LocalDateTime fechaHora;
    private EstadoEvento estado;
    private String politicasCancelacion;
    private String politicasReembolso;
    private Recinto recinto;
    private String politicas;
    private List<Observador> observadores = new ArrayList<>();

    // NUEVO: Zonas específicas del evento
    private List<Zona> zonas = new ArrayList<>();

    public Evento(String idEvento, String nombre, CategoriaEvento categoria, String descripcion,
                  String ciudad, LocalDateTime fechaHora, Recinto recinto) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.fechaHora = fechaHora;
        this.estado = EstadoEvento.BORRADOR;
        this.recinto = recinto;
        this.politicasCancelacion = "";
        this.politicasReembolso = "";
        this.politicas = "";
        this.zonas = new ArrayList<>();
    }

    // ========== GETTERS Y SETTERS EXISTENTES ==========
    public String getPoliticas() { return politicas; }
    public void setPoliticas(String politicas) { this.politicas = politicas; }

    public String getIdEvento() { return idEvento; }
    public void setIdEvento(String idEvento) { this.idEvento = idEvento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public CategoriaEvento getCategoria() { return categoria; }
    public void setCategoria(CategoriaEvento categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
        notificarObservadores("ESTADO_EVENTO", "El evento " + this.nombre + " cambió a " + estado);
    }

    public String getPoliticasCancelacion() { return politicasCancelacion; }
    public void setPoliticasCancelacion(String politicasCancelacion) { this.politicasCancelacion = politicasCancelacion; }

    public String getPoliticasReembolso() { return politicasReembolso; }
    public void setPoliticasReembolso(String politicasReembolso) { this.politicasReembolso = politicasReembolso; }

    public Recinto getRecinto() { return recinto; }
    public void setRecinto(Recinto recinto) { this.recinto = recinto; }

    // ========== NUEVOS MÉTODOS PARA ZONAS ==========
    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }

    public void agregarZona(Zona zona) {
        this.zonas.add(zona);
    }

    public void eliminarZona(Zona zona) {
        this.zonas.remove(zona);
    }

    // ========== MÉTODO PARA OBTENER PRECIO MÍNIMO ==========
    public double getPrecioMinimo() {
        return zonas.stream()
                .mapToDouble(Zona::getPrecioBase)
                .min()
                .orElse(0);
    }

    // ========== MÉTODO PARA OBTENER CAPACIDAD TOTAL ==========
    public int getCapacidadTotal() {
        return zonas.stream()
                .mapToInt(Zona::getCapacidad)
                .sum();
    }

    // ========== MÉTODO PARA OBTENER ASIENTOS DISPONIBLES TOTALES ==========
    public int getAsientosDisponiblesTotales() {
        return zonas.stream()
                .mapToInt(zona -> (int) zona.getAsientos().stream()
                        .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                        .count())
                .sum();
    }

    @Override
    public String toString() {
        return nombre + " - " + fechaHora + " (" + estado + ")";
    }

    // ========== MÉTODOS OBSERVER ==========
    public void agregarObservador(Observador obs) { observadores.add(obs); }
    public void removerObservador(Observador obs) { observadores.remove(obs); }
    public void notificarObservadores(String tipoEvento, String mensaje) {
        for (Observador obs : observadores) {
            obs.notificar(tipoEvento, mensaje, this);
        }
    }
}