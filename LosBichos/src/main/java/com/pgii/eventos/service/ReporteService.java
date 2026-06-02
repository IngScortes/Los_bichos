package com.pgii.eventos.service;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.structural.adapter.IReporteExporter;
import com.pgii.eventos.repository.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReporteService {
    private CompraRepository compraRepository;
    private EventoRepository eventoRepository;
    private UsuarioRepository usuarioRepository;

    public ReporteService(CompraRepository compraRepository,
                          EventoRepository eventoRepository,
                          UsuarioRepository usuarioRepository) {
        this.compraRepository = compraRepository;
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========== NUEVO MÉTODO PARA EXPORTAR VENTAS ==========
    public void exportarReporteVentas(IReporteExporter exporter, String ruta) throws Exception {
        List<Compra> compras = compraRepository.findAll();
        List<String[]> filas = new ArrayList<>();

        // Encabezados
        filas.add(new String[]{"ID Compra", "Usuario", "Email", "Evento", "Fecha", "Total", "Estado"});

        // Datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Compra c : compras) {
            filas.add(new String[]{
                    c.getIdCompra(),
                    c.getUsuario().getNombreCompleto(),
                    c.getUsuario().getEmail(),
                    c.getEvento().getNombre(),
                    c.getFechaCreacion().format(formatter),
                    String.valueOf(c.getTotal()),
                    c.getEstado().toString()
            });
        }

        exporter.exportar(filas, ruta);
        System.out.println("✅ Reporte exportado a: " + ruta);
    }

    // ========== NUEVO MÉTODO PARA EXPORTAR COMPRADORES POR EVENTO ==========
    public void exportarCompradoresPorEvento(String idEvento, IReporteExporter exporter, String ruta) throws Exception {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new Exception("Evento no encontrado: " + idEvento);
        }

        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> c.getEvento().getIdEvento().equals(idEvento))
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .toList();

        List<String[]> filas = new ArrayList<>();
        filas.add(new String[]{"ID Compra", "Comprador", "Email", "Teléfono", "Total", "Fecha"});

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Compra c : compras) {
            filas.add(new String[]{
                    c.getIdCompra(),
                    c.getUsuario().getNombreCompleto(),
                    c.getUsuario().getEmail(),
                    c.getUsuario().getTelefono(),
                    String.valueOf(c.getTotal()),
                    c.getFechaCreacion().format(formatter)
            });
        }

        exporter.exportar(filas, ruta);
        System.out.println("✅ Reporte de compradores exportado a: " + ruta);
    }

    // ========== NUEVO MÉTODO PARA EXPORTAR ESTADÍSTICAS ==========
    public void exportarEstadisticas(IReporteExporter exporter, String ruta) throws Exception {
        List<String[]> filas = new ArrayList<>();

        long totalEventos = eventoRepository.findAll().size();
        long totalEventosPublicados = eventoRepository.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long totalCompras = compraRepository.findAll().size();
        long totalComprasPagadas = compraRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA).count();
        double ingresosTotales = compraRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal).sum();
        long totalUsuarios = usuarioRepository.findAll().size();

        filas.add(new String[]{"Métrica", "Valor"});
        filas.add(new String[]{"Total Eventos", String.valueOf(totalEventos)});
        filas.add(new String[]{"Eventos Publicados", String.valueOf(totalEventosPublicados)});
        filas.add(new String[]{"Total Compras", String.valueOf(totalCompras)});
        filas.add(new String[]{"Compras Pagadas", String.valueOf(totalComprasPagadas)});
        filas.add(new String[]{"Ingresos Totales", "$" + String.format("%,.0f", ingresosTotales)});
        filas.add(new String[]{"Total Usuarios", String.valueOf(totalUsuarios)});

        exporter.exportar(filas, ruta);
        System.out.println("✅ Estadísticas exportadas a: " + ruta);
    }
    public void exportarReporteVentasPDF(IReporteExporter exporter, String ruta) throws Exception {
        List<Compra> compras = compraRepository.findAll();
        List<String[]> filas = new ArrayList<>();

        filas.add(new String[]{"ID Compra", "Usuario", "Email", "Evento", "Fecha", "Total", "Estado"});

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Compra c : compras) {
            filas.add(new String[]{
                    c.getIdCompra(),
                    c.getUsuario().getNombreCompleto(),
                    c.getUsuario().getEmail(),
                    c.getEvento().getNombre(),
                    c.getFechaCreacion().format(formatter),
                    String.valueOf(c.getTotal()),
                    c.getEstado().toString()
            });
        }

        exporter.exportar(filas, ruta);
    }

    public void exportarEstadisticasPDF(IReporteExporter exporter, String ruta) throws Exception {
        List<String[]> filas = new ArrayList<>();

        long totalEventos = eventoRepository.findAll().size();
        long totalEventosPublicados = eventoRepository.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long totalCompras = compraRepository.findAll().size();
        long totalComprasPagadas = compraRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA).count();
        double ingresosTotales = compraRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal).sum();
        long totalUsuarios = usuarioRepository.findAll().size();

        filas.add(new String[]{"Métrica", "Valor"});
        filas.add(new String[]{"Total Eventos", String.valueOf(totalEventos)});
        filas.add(new String[]{"Eventos Publicados", String.valueOf(totalEventosPublicados)});
        filas.add(new String[]{"Total Compras", String.valueOf(totalCompras)});
        filas.add(new String[]{"Compras Pagadas", String.valueOf(totalComprasPagadas)});
        filas.add(new String[]{"Ingresos Totales", "$" + String.format("%,.0f", ingresosTotales)});
        filas.add(new String[]{"Total Usuarios", String.valueOf(totalUsuarios)});

        exporter.exportar(filas, ruta);
    }
}