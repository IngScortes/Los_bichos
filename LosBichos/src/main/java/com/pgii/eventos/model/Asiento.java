package com.pgii.eventos.model;

public class Asiento {
    private String idAsiento;
    private String fila;
    private int numero;
    private EstadoAsiento estado;
    private Zona zona;

    public Asiento(String idAsiento, String fila, int numero, Zona zona) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.estado = EstadoAsiento.DISPONIBLE;
        this.zona = zona;
    }

    // Getters y setters
    public String getIdAsiento() { return idAsiento; }
    public void setIdAsiento(String idAsiento) { this.idAsiento = idAsiento; }

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public EstadoAsiento getEstado() { return estado; }
    public void setEstado(EstadoAsiento estado) { this.estado = estado; }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    @Override
    public String toString() {
        return "Asiento " + fila + numero + " (" + estado + ")";
    }
}