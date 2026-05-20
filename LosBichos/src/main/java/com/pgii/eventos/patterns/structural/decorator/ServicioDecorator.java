package com.pgii.eventos.patterns.structural.decorator;

import com.pgii.eventos.model.ItemCompra;

public abstract class ServicioDecorator implements ItemCompra {
    protected ItemCompra wrapped;

    public ServicioDecorator(ItemCompra wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public double getPrecio() {
        return wrapped.getPrecio();
    }

    @Override
    public String getDescripcion() {
        return wrapped.getDescripcion();
    }
}
