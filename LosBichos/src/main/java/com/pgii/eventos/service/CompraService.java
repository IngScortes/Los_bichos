package com.pgii.eventos.service;

import com.pgii.eventos.model.*;

import com.pgii.eventos.patterns.behavioral.strategy.MetodoPago;
import com.pgii.eventos.patterns.behavioral.strategy.ResultadoPago;
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

    public ResultadoPago pagarCompraConStrategy(Compra compra, MetodoPago metodo) {
        if (compra.getEstado() != EstadoCompra.CREADA) {
            throw new IllegalStateException("Compra no está en estado CREADA");
        }
        ResultadoPago resultado = metodo.procesarPago(compra.getTotal());
        if (resultado.isExitoso()) {
            Pago pago = new Pago("PAG" + System.currentTimeMillis(), metodo.getClass().getSimpleName(), compra.getTotal(), LocalDateTime.now());
            pago.setEstado(EstadoPago.APROBADO);
            compra.setPago(pago);
            compra.setEstado(EstadoCompra.PAGADA);
            compraRepository.save(compra);
        }
        return resultado;
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
