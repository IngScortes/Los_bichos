package com.pgii.eventos.repository;

import com.pgii.eventos.model.Incidencia;

public class IncidenciaRepository extends InMemoryRepository<Incidencia> {
    private static IncidenciaRepository instance;

    public static IncidenciaRepository getInstance() {
        if (instance == null) {
            instance = new IncidenciaRepository();
        }
        return instance;
    }

    private IncidenciaRepository() {
        super(Incidencia::getIdIncidencia);
    }
}