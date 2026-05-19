package com.pgii.eventos.model;
import java.util.ArrayList;
import java.util.List;

public class Usuario extends Persona{
    private List<String> metodosPago;
    private List<Object> compras;
    public Usuario(String id, String nombreCompleto, String email, String telefono) {
        super(id, nombreCompleto, email, telefono);
        this.metodosPago = new ArrayList<>();
        this.compras = new ArrayList<>();
    }
    public List<String> getMetodosPago() {
        return metodosPago;
    }

    public void agregarMetodoPago(String metodo) {
        this.metodosPago.add(metodo);
    }

    public void eliminarMetodoPago(String metodo) {
        this.metodosPago.remove(metodo);
    }

    public List<Object> getCompras() {
        return compras;
    }

    public void agregarCompra(Object compra) {
        this.compras.add(compra);
    }
}
