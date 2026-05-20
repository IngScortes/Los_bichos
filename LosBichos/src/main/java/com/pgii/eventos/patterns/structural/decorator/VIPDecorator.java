package com.pgii.eventos.patterns.structural.decorator;

import com.pgii.eventos.model.ItemCompra;

public class VIPDecorator extends ServicioDecorator {
    private static final double COSTO = 50_000;

    public VIPDecorator(ItemCompra wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + COSTO;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Acceso VIP";
    }
}