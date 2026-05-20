package com.pgii.eventos.model;

import com.pgii.eventos.patterns.behavioral.observer.Observador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime fechaCreacion;
    private double total;
    private EstadoCompra estado;
    private List<ItemCompra> items;
    private Pago pago;
    private List<Observador> observadores = new ArrayList<>();



    public Compra(String idCompra, Usuario usuario, Evento evento) {
        this.idCompra = idCompra;
        this.usuario = usuario;
        this.evento = evento;
        this.fechaCreacion = LocalDateTime.now();
        this.total = 0.0;
        this.estado = EstadoCompra.CREADA;
        this.items = new ArrayList<>();
        this.pago = null;
    }

    // Método para agregar un item y actualizar total
    public void agregarItem(ItemCompra item) {
        items.add(item);
        recalcularTotal();
    }

    public void eliminarItem(ItemCompra item) {
        items.remove(item);
        recalcularTotal();
    }

    private void recalcularTotal() {
        total = items.stream().mapToDouble(ItemCompra::getPrecio).sum();
    }

    public String getIdCompra() { return idCompra; }
    public void setIdCompra(String idCompra) { this.idCompra = idCompra; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }

    public List<ItemCompra> getItems() { return items; }
    public void setItems(List<ItemCompra> items) { this.items = items; recalcularTotal(); }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public void agregarObservador(Observador obs) { observadores.add(obs); }
    public void removerObservador(Observador obs) { observadores.remove(obs); }

    public void notificarObservadores(String tipoEvento, String mensaje) {
        for (Observador obs : observadores) {
            obs.notificar(tipoEvento, mensaje, this);
        }
    }

    @Override
    public String toString() {
        return "Compra " + idCompra + " - Total: " + total + " - Estado: " + estado;
    }
}