package com.pgii.eventos.patterns.behavioral.command;

import com.pgii.eventos.model.Evento;
import com.pgii.eventos.model.EstadoEvento;
import com.pgii.eventos.service.EventoService;

public class PublicarEventoComando implements Comando {
    private Evento evento;
    private EventoService eventoService;
    private EstadoEvento estadoAnterior;

    public PublicarEventoComando(Evento evento, EventoService eventoService) {
        this.evento = evento;
        this.eventoService = eventoService;
    }

    @Override
    public void ejecutar() {
        estadoAnterior = evento.getEstado();
        eventoService.cambiarEstadoEvento(evento.getIdEvento(), EstadoEvento.PUBLICADO);
        System.out.println("Evento " + evento.getIdEvento() + " publicado.");
    }

    @Override
    public void deshacer() {
        eventoService.cambiarEstadoEvento(evento.getIdEvento(), estadoAnterior);
        System.out.println("Undo: Estado del evento restaurado a " + estadoAnterior);
    }
}
