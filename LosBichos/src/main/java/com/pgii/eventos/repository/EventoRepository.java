package com.pgii.eventos.repository;

import com.pgii.eventos.model.Evento;

public class EventoRepository extends InMemoryRepository<Evento> {
    private static EventoRepository instance;

    public static EventoRepository getInstance() {
        if (instance == null) {
            instance = new EventoRepository();
        }
        return instance;
    }

    private EventoRepository() {
        super(Evento::getIdEvento);
    }
}