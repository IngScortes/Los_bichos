package com.pgii.eventos.model;

import java.time.LocalDateTime;

public class Pago {
    private String idPago;
    private String metodo;
    private double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;

    public Pago(String idPago, String metodo, double monto, LocalDateTime fecha) {
        this.idPago = idPago;
        this.metodo = metodo;
        this.monto = monto;
        this.fecha = fecha;
        this.estado = EstadoPago.PENDIENTE;
    }

    public String getIdPago() { return idPago; }
    public void setIdPago(String idPago) { this.idPago = idPago; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
}