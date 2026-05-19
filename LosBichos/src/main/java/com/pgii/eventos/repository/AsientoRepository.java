package com.pgii.eventos.repository;

import com.pgii.eventos.model.Asiento;

public class AsientoRepository extends InMemoryRepository<Asiento> {
    public AsientoRepository() {
        super(Asiento::getIdAsiento);
    }
}
