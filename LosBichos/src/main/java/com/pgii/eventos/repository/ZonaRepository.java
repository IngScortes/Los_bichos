package com.pgii.eventos.repository;

import com.pgii.eventos.model.Zona;

public class ZonaRepository extends InMemoryRepository<Zona> {
    public ZonaRepository() {
        super(Zona::getIdZona);
    }
}