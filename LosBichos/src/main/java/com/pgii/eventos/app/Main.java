package com.pgii.eventos.app;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.structural.adapter.ApachePOICSVAdapter;
import com.pgii.eventos.patterns.structural.adapter.IReporteExporter;
import com.pgii.eventos.patterns.structural.adapter.PDFBoxAdapter;
import com.pgii.eventos.patterns.structural.decorator.MerchandisingDecorator;
import com.pgii.eventos.patterns.structural.decorator.ParqueaderoDecorator;
import com.pgii.eventos.patterns.structural.decorator.SeguroDecorator;
import com.pgii.eventos.patterns.structural.decorator.VIPDecorator;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import com.pgii.eventos.patterns.creational.builder.CompraBuilder;
import com.pgii.eventos.patterns.creational.factory.EventoFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Repositorios
        RecintoRepository recintoRepo = new RecintoRepository();
        ZonaRepository zonaRepo = new ZonaRepository();
        AsientoRepository asientoRepo = new AsientoRepository();
        EventoRepository eventoRepo = new EventoRepository();
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        AdministradorRepository adminRepo = new AdministradorRepository();
        CompraRepository compraRepo = new CompraRepository();

        // Inicializar datos
        DataInitializer.inicializar(recintoRepo, zonaRepo, asientoRepo, eventoRepo,
                usuarioRepo, adminRepo, compraRepo);

        // Servicios
        UsuarioService usuarioService = new UsuarioService(usuarioRepo);
        EventoService eventoService = new EventoService(eventoRepo);
        CompraService compraService = new CompraService(compraRepo, asientoRepo);
        AdminService adminService = new AdminService(usuarioRepo, eventoRepo, recintoRepo, compraRepo);

        // ========== PRUEBAS DE FUNCIONALIDAD ==========
        // 1. Login de usuario
        Usuario juan = usuarioService.login("juan@mail.com", "300111222");
        if (juan != null) {
            GestorSesion.getInstance().setUsuarioActivo(juan);
            System.out.println("Login exitoso: " + juan.getNombreCompleto());
        }

        // 2. Explorar eventos publicados
        System.out.println("\nEventos publicados:");
        eventoService.listarEventosPublicados().forEach(e -> System.out.println(" - " + e.getNombre()));

        // 3. Crear una compra para juan (evento E001)
        Evento eventoJuanes = eventoService.buscarEvento("E001");
        Compra nuevaCompra = compraService.crearCompra("C004", juan, eventoJuanes);

        // Agregar una entrada (buscar asiento VIP disponible)
        Zona zonaVIP = zonaRepo.findAll().stream().filter(z -> z.getNombre().equals("VIP")).findFirst().orElse(null);
        Asiento asientoLibre = null;
        if (zonaVIP != null) {
            asientoLibre = zonaVIP.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).findFirst().orElse(null);
        }
        if (asientoLibre != null) {
            Entrada entrada = new Entrada("ENT004", eventoJuanes, zonaVIP, asientoLibre, zonaVIP.getPrecioBase());
            compraService.agregarEntrada(nuevaCompra, entrada);
            System.out.println("\nEntrada agregada: " + entrada.getDescripcion());
        }

        // 4. Pagar compra
        compraService.pagarCompra(nuevaCompra, "Tarjeta crédito");
        System.out.println("Compra pagada. Estado: " + nuevaCompra.getEstado());

        // 5. Ver historial de compras de Juan
        System.out.println("\nHistorial de compras de Juan:");
        juan.getCompras().forEach(c -> System.out.println(" - Compra " + c.getIdCompra() + " Total: $" + c.getTotal() + " Estado: " + c.getEstado()));

        // 6. Administrador: listar eventos y pausar uno
        System.out.println("\nAdministrador: lista de eventos");
        adminService.listarEventos().forEach(e -> System.out.println(e.getNombre() + " [" + e.getEstado() + "]"));
        adminService.pausarEvento("E001");
        System.out.println("Evento E001 pausado. Nuevo estado: " + eventoService.buscarEvento("E001").getEstado());

        // ========== DEMOSTRACIÓN DE PATRONES CREACIONALES Y ESTRUCTURALES ==========
        System.out.println("\n=== Demostración de patrones creacionales ===");
        // Factory Method
        Recinto algunRecinto = recintoRepo.findAll().isEmpty() ? null : recintoRepo.findAll().get(0);
        if (algunRecinto != null) {
            Evento nuevoEvento = EventoFactory.crearEvento("E005", "Noche de Ópera", CategoriaEvento.TEATRO,
                    "Ópera en vivo", "Armenia", LocalDateTime.now().plusMonths(2), algunRecinto);
            System.out.println("Evento creado con Factory: " + nuevoEvento.getNombre() + " - Políticas: " + nuevoEvento.getPoliticas());
        }

        // Builder y Decorator
        if (zonaVIP != null && asientoLibre != null && eventoJuanes != null) {
            ItemCompra entradaBase = new Entrada("ENT100", eventoJuanes, zonaVIP, asientoLibre, zonaVIP.getPrecioBase());
            ItemCompra entradaConVIP = new VIPDecorator(entradaBase);
            ItemCompra entradaConVIPySeguro = new SeguroDecorator(entradaConVIP);
            ItemCompra entradaCompleta = new ParqueaderoDecorator(new MerchandisingDecorator(entradaConVIPySeguro));

            System.out.println("Entrada base: $" + entradaBase.getPrecio());
            System.out.println("+ VIP: $" + entradaConVIP.getPrecio());
            System.out.println("+ Seguro: $" + entradaConVIPySeguro.getPrecio());
            System.out.println("+ Merchandising + Parqueadero: $" + entradaCompleta.getPrecio());
            System.out.println("Descripción final: " + entradaCompleta.getDescripcion());
        }

        // ========== DEMOSTRACIÓN DE ADAPTER (REPORTES) ==========
        System.out.println("\n=== Generando reporte con Adapter ===");
        try {
            ReporteService reporteService = new ReporteService(compraRepo);
            IReporteExporter csvExporter = new ApachePOICSVAdapter();
            IReporteExporter pdfExporter = new PDFBoxAdapter();
            reporteService.generarReporteVentasPorPeriodo(LocalDate.now().minusMonths(1), LocalDate.now(), csvExporter, "ventas.xlsx");
            System.out.println("Reporte CSV/Excel generado en ventas.xlsx");
            // También puedes generar PDF:
            // reporteService.generarReporteVentasPorPeriodo(LocalDate.now().minusMonths(1), LocalDate.now(), pdfExporter, "ventas.pdf");
            // System.out.println("Reporte PDF generado en ventas.pdf");
        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nBackend funcionando correctamente.");
    }
}