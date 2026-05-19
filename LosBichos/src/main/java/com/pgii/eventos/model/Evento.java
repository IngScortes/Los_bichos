package com.pgii.eventos.model;

import java.time.LocalDateTime;

public class Evento {
    private String idEvento;
    private String nombre;
    private CategoriaEvento categoria;
    private String descripcion;
    private String ciudad;
    private LocalDateTime fechaHora;
    private EstadoEvento estado;
    private String politicasCancelacion; // texto simple
    private String politicasReembolso;
    private Recinto recinto;
    private String politicas;

    public Evento(String idEvento, String nombre, CategoriaEvento categoria, String descripcion,
                  String ciudad, LocalDateTime fechaHora, Recinto recinto) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.fechaHora = fechaHora;
        this.estado = EstadoEvento.BORRADOR; // por defecto
        this.recinto = recinto;
        this.politicasCancelacion = "";
        this.politicasReembolso = "";
        this.politicas = "";
    }

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
    public void setEstado(EstadoEvento estado) { this.estado = estado; }

    public String getPoliticasCancelacion() { return politicasCancelacion; }
    public void setPoliticasCancelacion(String politicasCancelacion) { this.politicasCancelacion = politicasCancelacion; }

    public String getPoliticasReembolso() { return politicasReembolso; }
    public void setPoliticasReembolso(String politicasReembolso) { this.politicasReembolso = politicasReembolso; }

    public Recinto getRecinto() { return recinto; }
    public void setRecinto(Recinto recinto) { this.recinto = recinto; }

    @Override
    public String toString() {
        return nombre + " - " + fechaHora + " (" + estado + ")";
    }
}