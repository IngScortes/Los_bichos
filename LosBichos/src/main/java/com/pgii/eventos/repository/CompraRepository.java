package com.pgii.eventos.repository;

import com.pgii.eventos.model.Compra;

public class CompraRepository extends InMemoryRepository<Compra> {
    private static CompraRepository instance;

    public static CompraRepository getInstance() {
        if (instance == null) {
            instance = new CompraRepository();
        }
        return instance;
    }

    private CompraRepository() {
        super(Compra::getIdCompra);
    }
}