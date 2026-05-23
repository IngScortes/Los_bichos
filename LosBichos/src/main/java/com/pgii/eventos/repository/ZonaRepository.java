package com.pgii.eventos.repository;

import com.pgii.eventos.model.Zona;

public class ZonaRepository extends InMemoryRepository<Zona> {
    private static ZonaRepository instance;

    public static ZonaRepository getInstance() {
        if (instance == null) {
            instance = new ZonaRepository();
        }
        return instance;
    }

    private ZonaRepository() {
        super(Zona::getIdZona);
    }
}