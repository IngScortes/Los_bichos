package com.pgii.eventos.repository;

import com.pgii.eventos.model.Asiento;

public class AsientoRepository extends InMemoryRepository<Asiento> {
    private static AsientoRepository instance;

    public static AsientoRepository getInstance() {
        if (instance == null) {
            instance = new AsientoRepository();
        }
        return instance;
    }

    private AsientoRepository() {
        super(Asiento::getIdAsiento);
    }
}