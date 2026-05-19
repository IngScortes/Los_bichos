package com.pgii.eventos.patterns.creational.builder;

import com.pgii.eventos.model.*;
import java.util.ArrayList;
import java.util.List;

public class CompraBuilder {
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private List<ItemCompra> items;

    public CompraBuilder() {
        this.items = new ArrayList<>();
    }

    public CompraBuilder setIdCompra(String idCompra) {
        this.idCompra = idCompra;
        return this;
    }

    public CompraBuilder setUsuario(Usuario usuario) {
        this.usuario = usuario;
        return this;
    }

    public CompraBuilder setEvento(Evento evento) {
        this.evento = evento;
        return this;
    }

    public CompraBuilder addItem(ItemCompra item) {
        this.items.add(item);
        return this;
    }

    public CompraBuilder addItems(List<ItemCompra> items) {
        this.items.addAll(items);
        return this;
    }

    public Compra build() {
        if (idCompra == null || usuario == null || evento == null) {
            throw new IllegalStateException("idCompra, usuario y evento son obligatorios");
        }
        Compra compra = new Compra(idCompra, usuario, evento);
        for (ItemCompra item : items) {
            compra.agregarItem(item);
        }
        return compra;
    }
}