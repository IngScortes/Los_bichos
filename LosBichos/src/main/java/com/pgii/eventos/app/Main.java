package com.pgii.eventos.app;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;

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

        // 3. Crear una compra para juan (evento Juanes)
        Evento eventoJuanes = eventoService.buscarEvento("E001");
        Compra nuevaCompra = compraService.crearCompra("C004", juan, eventoJuanes);

        // Agregar una entrada (buscar asiento VIP disponible)
        Zona zonaVIP = zonaRepo.findAll().stream().filter(z -> z.getNombre().equals("VIP")).findFirst().orElse(null);
        Asiento asientoLibre = zonaVIP.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).findFirst().orElse(null);
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

        System.out.println("\nBackend funcionando correctamente.");
    }
}