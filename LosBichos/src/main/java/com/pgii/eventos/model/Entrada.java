package com.pgii.eventos.model;

public class Entrada implements ItemCompra {
    private String idEntrada;
    private Evento evento;
    private Zona zona;
    private Asiento asiento;
    private double precioFinal;
    private String estado;
    public Entrada(String idEntrada, Evento evento, Zona zona, Asiento asiento, double precioFinal) {
        this.idEntrada = idEntrada;
        this.evento = evento;
        this.zona = zona;
        this.asiento = asiento;
        this.precioFinal = precioFinal;
        this.estado = "ACTIVA";
    }

    public String getIdEntrada() { return idEntrada; }
    public void setIdEntrada(String idEntrada) { this.idEntrada = idEntrada; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    public Asiento getAsiento() { return asiento; }
    public void setAsiento(Asiento asiento) { this.asiento = asiento; }

    public double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(double precioFinal) { this.precioFinal = precioFinal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public double getPrecio() {
        return precioFinal;
    }

    @Override
    public String getDescripcion() {
        String asientoDesc = (asiento != null) ? " Asiento " + asiento.getFila() + asiento.getNumero() : " Entrada general";
        return "Entrada para " + evento.getNombre() + " - Zona " + zona.getNombre() + asientoDesc;
    }
}