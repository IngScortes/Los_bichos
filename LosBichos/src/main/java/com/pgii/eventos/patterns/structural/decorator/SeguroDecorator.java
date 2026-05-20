package com.pgii.eventos.patterns.structural.decorator;

import com.pgii.eventos.model.ItemCompra;

public class SeguroDecorator extends ServicioDecorator {
    private static final double COSTO = 15_000;

    public SeguroDecorator(ItemCompra wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + COSTO;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Seguro de cancelación";
    }
}
