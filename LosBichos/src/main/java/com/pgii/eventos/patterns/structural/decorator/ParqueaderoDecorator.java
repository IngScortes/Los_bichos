package com.pgii.eventos.patterns.structural.decorator;

import com.pgii.eventos.model.ItemCompra;

public class ParqueaderoDecorator extends ServicioDecorator {
    private static final double COSTO = 10_000;

    public ParqueaderoDecorator(ItemCompra wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + COSTO;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Parqueadero preferencial";
    }
}