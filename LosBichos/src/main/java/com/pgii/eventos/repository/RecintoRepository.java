package com.pgii.eventos.repository;

import com.pgii.eventos.model.Recinto;

public class RecintoRepository extends InMemoryRepository<Recinto> {
    private static RecintoRepository instance;

    public static RecintoRepository getInstance() {
        if (instance == null) {
            instance = new RecintoRepository();
        }
        return instance;
    }

    private RecintoRepository() {
        super(Recinto::getIdRecinto);
    }
}