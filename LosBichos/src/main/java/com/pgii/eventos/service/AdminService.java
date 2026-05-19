package com.pgii.eventos.service;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;

import java.util.List;

public class AdminService {
    private UsuarioRepository usuarioRepository;
    private EventoRepository eventoRepository;
    private RecintoRepository recintoRepository;
    private CompraRepository compraRepository;

    public AdminService(UsuarioRepository usuarioRepository,
                        EventoRepository eventoRepository,
                        RecintoRepository recintoRepository,
                        CompraRepository compraRepository) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.recintoRepository = recintoRepository;
        this.compraRepository = compraRepository;
    }

    // Gestión de usuarios
    public List<Usuario> listarUsuarios() { return usuarioRepository.findAll(); }
    public void eliminarUsuario(String id) { usuarioRepository.deleteById(id); }

    // Gestión de eventos
    public List<Evento> listarEventos() { return eventoRepository.findAll(); }
    public void publicarEvento(String idEvento) { cambiarEstado(idEvento, EstadoEvento.PUBLICADO); }
    public void pausarEvento(String idEvento) { cambiarEstado(idEvento, EstadoEvento.PAUSADO); }
    public void cancelarEvento(String idEvento) { cambiarEstado(idEvento, EstadoEvento.CANCELADO); }
    private void cambiarEstado(String id, EstadoEvento estado) {
        Evento e = eventoRepository.findById(id);
        if (e != null) {
            e.setEstado(estado);
            eventoRepository.save(e);
        }
    }

    // Gestión de recintos
    public void agregarRecinto(Recinto r) { recintoRepository.save(r); }
    public List<Recinto> listarRecintos() { return recintoRepository.findAll(); }

    // Gestión de compras
    public List<Compra> listarCompras() { return compraRepository.findAll(); }
    public void reembolsarCompra(String idCompra) {
        Compra c = compraRepository.findById(idCompra);
        if (c != null && c.getEstado() == EstadoCompra.PAGADA) {
            c.setEstado(EstadoCompra.REEMBOLSADA);
            // Liberar asientos
            for (ItemCompra item : c.getItems()) {
                if (item instanceof Entrada && ((Entrada) item).getAsiento() != null) {
                    ((Entrada) item).getAsiento().setEstado(EstadoAsiento.DISPONIBLE);
                }
            }
            compraRepository.save(c);
        }
    }
}