package com.pgii.eventos.service;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.behavioral.strategy.MetodoPago;
import com.pgii.eventos.patterns.behavioral.strategy.ResultadoPago;
import com.pgii.eventos.repository.CompraRepository;
import com.pgii.eventos.repository.AsientoRepository;
import com.pgii.eventos.repository.IncidenciaRepository;
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
        if (entrada.getAsiento() != null) {
            entrada.getAsiento().setEstado(EstadoAsiento.RESERVADO);
            asientoRepository.save(entrada.getAsiento());
        }
        compraRepository.save(compra);
    }

    public void cancelarCompra(Compra compra) {
        compra.setEstado(EstadoCompra.CANCELADA);
        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada && ((Entrada) item).getAsiento() != null) {
                ((Entrada) item).getAsiento().setEstado(EstadoAsiento.DISPONIBLE);
                asientoRepository.save(((Entrada) item).getAsiento());
            }
        }
        compraRepository.save(compra);
    }

    public ResultadoPago pagarCompraConStrategy(Compra compra, MetodoPago metodo) {
        if (compra.getEstado() != EstadoCompra.CREADA) {
            // Registrar incidencia
            Incidencia incidencia = new Incidencia(
                    "INC_" + System.currentTimeMillis(),
                    "Intento de pago inválido",
                    "Usuario " + compra.getUsuario().getEmail() + " intentó pagar compra " +
                            compra.getIdCompra() + " en estado " + compra.getEstado(),
                    Incidencia.Tipo.TECNICA,
                    Incidencia.Prioridad.ALTA,
                    compra.getEvento(),
                    "Sistema"
            );
            IncidenciaRepository.getInstance().save(incidencia);

            return new ResultadoPago(false, "La compra no está en estado CREADA", null);
        }

        ResultadoPago resultado = metodo.procesarPago(compra.getTotal());

        if (resultado.isExitoso()) {
            Pago pago = new Pago("PAG" + System.currentTimeMillis(),
                    metodo.getClass().getSimpleName(),
                    compra.getTotal(),
                    LocalDateTime.now());
            pago.setEstado(EstadoPago.APROBADO);
            compra.setPago(pago);
            compra.setEstado(EstadoCompra.PAGADA);

            for (ItemCompra item : compra.getItems()) {
                if (item instanceof Entrada && ((Entrada) item).getAsiento() != null) {
                    ((Entrada) item).getAsiento().setEstado(EstadoAsiento.VENDIDO);
                    asientoRepository.save(((Entrada) item).getAsiento());
                }
            }

            compraRepository.save(compra);
        }

        return resultado;
    }

    public Compra buscarCompra(String id) {
        return compraRepository.findById(id);
    }
}