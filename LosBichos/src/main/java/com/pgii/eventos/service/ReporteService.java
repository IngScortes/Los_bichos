package com.pgii.eventos.service;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.structural.adapter.IReporteExporter;
import com.pgii.eventos.repository.CompraRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteService {
    private CompraRepository compraRepository;

    public ReporteService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public void generarReporteVentasPorPeriodo(LocalDate desde, LocalDate hasta, IReporteExporter exporter, String ruta) throws Exception {
        List<Compra> compras = compraRepository.findAll();
        List<String[]> filas = new ArrayList<>();
        filas.add(new String[]{"ID Compra", "Usuario", "Evento", "Fecha", "Total", "Estado"});
        for (Compra c : compras) {
            LocalDateTime fecha = c.getFechaCreacion();
            if (fecha.toLocalDate().isAfter(desde.minusDays(1)) && fecha.toLocalDate().isBefore(hasta.plusDays(1))) {
                filas.add(new String[]{
                        c.getIdCompra(),
                        c.getUsuario().getNombreCompleto(),
                        c.getEvento().getNombre(),
                        fecha.toString(),
                        String.valueOf(c.getTotal()),
                        c.getEstado().toString()
                });
            }
        }
        exporter.exportar(filas, ruta);
    }

}