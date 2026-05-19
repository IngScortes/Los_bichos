package com.pgii.eventos.repository;

import com.pgii.eventos.model.Compra;

public class CompraRepository extends InMemoryRepository<Compra> {
    public CompraRepository() {
        super(Compra::getIdCompra);
    }
}