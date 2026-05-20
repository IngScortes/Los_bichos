package com.pgii.eventos.service;

import com.pgii.eventos.model.*;

import com.pgii.eventos.patterns.creational.builder.CompraBuilder;
import com.pgii.eventos.patterns.structural.decorator.MerchandisingDecorator;
import com.pgii.eventos.patterns.structural.decorator.ParqueaderoDecorator;
import com.pgii.eventos.patterns.structural.decorator.VIPDecorator;
import com.pgii.eventos.repository.CompraRepository;
import com.pgii.eventos.repository.AsientoRepository;

import java.time.LocalDateTime;

public class CompraService {
    private CompraRepository compraRepository;
    private AsientoRepository asientoRepository;

    public CompraService(CompraRepository compraRepository, AsientoRepository asientoRepository) {
        this.compraRepository = compraRepository;
        this.asientoRepository = asientoRepository;
    }

    public Compra crearCompra(String idCompra, Usuario usuario, Evento evento) {
        Compra compra = new Compra(idCompra, usuario, evento);
        compraRepository.save(compra);
        usuario.agregarCompra(compra);
        return compra;
    }

    public void agregarEntrada(Compra compra, Entrada entrada) {
        compra.agregarItem(entrada);
        // Marcar asiento como RESERVADO si aplica
        if (entrada.getAsiento() != null) {
            entrada.getAsiento().setEstado(EstadoAsiento.RESERVADO);
            asientoRepository.save(entrada.getAsiento());
        }
        compraRepository.save(compra);
    }

    public void cancelarCompra(Compra compra) {
        if (compra.getEstado() == EstadoCompra.PAGADA || compra.getEstado() == EstadoCompra.CONFIRMADA) {
            // Aquí luego aplicaremos reglas de reembolso, por ahora solo cambiamos estado
            compra.setEstado(EstadoCompra.CANCELADA);
            // Liberar asientos
            for (ItemCompra item : compra.getItems()) {
                if (item instanceof Entrada) {
                    Entrada e = (Entrada) item;
                    if (e.getAsiento() != null && e.getAsiento().getEstado() == EstadoAsiento.VENDIDO) {
                        e.getAsiento().setEstado(EstadoAsiento.DISPONIBLE);
                        asientoRepository.save(e.getAsiento());
                    }
                }
            }
        } else if (compra.getEstado() == EstadoCompra.CREADA) {
            compra.setEstado(EstadoCompra.CANCELADA);
            // Liberar reservas
            for (ItemCompra item : compra.getItems()) {
                if (item instanceof Entrada) {
                    Entrada e = (Entrada) item;
                    if (e.getAsiento() != null && e.getAsiento().getEstado() == EstadoAsiento.RESERVADO) {
                        e.getAsiento().setEstado(EstadoAsiento.DISPONIBLE);
                        asientoRepository.save(e.getAsiento());
                    }
                }
            }
        }
        compraRepository.save(compra);
    }

    public void pagarCompra(Compra compra, String metodoPago) {
        if (compra.getEstado() != EstadoCompra.CREADA) {
            throw new IllegalStateException("La compra ya fue pagada o cancelada");
        }
        Pago pago = new Pago("PAG" + System.currentTimeMillis(), metodoPago, compra.getTotal(), LocalDateTime.now());
        pago.setEstado(EstadoPago.APROBADO); // simulación
        compra.setPago(pago);
        compra.setEstado(EstadoCompra.PAGADA);
        // Cambiar estado de asientos a VENDIDO
        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada e = (Entrada) item;
                if (e.getAsiento() != null) {
                    e.getAsiento().setEstado(EstadoAsiento.VENDIDO);
                    asientoRepository.save(e.getAsiento());
                }
            }
        }
        compraRepository.save(compra);
    }

    public Compra buscarCompra(String id) {
        return compraRepository.findById(id);
    }
    public Compra crearCompraConBuilder(CompraBuilder builder) {
        Compra compra = builder.build();
        compraRepository.save(compra);
        compra.getUsuario().agregarCompra(compra);
        return compra;
    }


}
