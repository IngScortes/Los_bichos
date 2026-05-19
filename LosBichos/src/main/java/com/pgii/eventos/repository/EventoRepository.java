package com.pgii.eventos.repository;

import com.pgii.eventos.model.Evento;

public class EventoRepository extends InMemoryRepository<Evento> {
    public EventoRepository() {
        super(Evento::getIdEvento);
    }
}