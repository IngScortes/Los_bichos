package com.pgii.eventos.service;

import com.pgii.eventos.model.CategoriaEvento;
import com.pgii.eventos.model.Evento;
import com.pgii.eventos.model.EstadoEvento;
import com.pgii.eventos.repository.EventoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventoService {
    private EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> listarEventosPublicados() {
        return eventoRepository.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .collect(Collectors.toList());
    }

    public List<Evento> filtrarEventos(String ciudad, CategoriaEvento categoria, LocalDateTime fechaDesde, Double precioMaximo) {
        return eventoRepository.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .filter(e -> ciudad == null || e.getCiudad().equalsIgnoreCase(ciudad))
                .filter(e -> categoria == null || e.getCategoria() == categoria)
                .filter(e -> fechaDesde == null || e.getFechaHora().isAfter(fechaDesde))
                .filter(e -> precioMaximo == null || (e.getRecinto().getZonas().stream().anyMatch(z -> z.getPrecioBase() <= precioMaximo)))
                .collect(Collectors.toList());
    }

    public Evento buscarEvento(String id) {
        return eventoRepository.findById(id);
    }

    // Métodos para administrador
    public void crearEvento(Evento evento) {
        eventoRepository.save(evento);
    }

    public void actualizarEvento(Evento evento) {
        eventoRepository.save(evento);
    }

    public void eliminarEvento(String id) {
        eventoRepository.deleteById(id);
    }

    public void cambiarEstadoEvento(String id, EstadoEvento nuevoEstado) {
        Evento e = eventoRepository.findById(id);
        if (e != null) {
            e.setEstado(nuevoEstado);
            eventoRepository.save(e);
        }
    }
}
