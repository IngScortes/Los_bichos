package com.pgii.eventos.repository;

import com.pgii.eventos.model.Recinto;

public class RecintoRepository extends InMemoryRepository<Recinto> {
    public RecintoRepository() {
        super(Recinto::getIdRecinto);
    }
}