package com.pgii.eventos.patterns.structural.decorator;

import com.pgii.eventos.model.ItemCompra;

public class MerchandisingDecorator extends ServicioDecorator {
    private static final double COSTO = 25_000;

    public MerchandisingDecorator(ItemCompra wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + COSTO;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Kit de merchandising";
    }
}