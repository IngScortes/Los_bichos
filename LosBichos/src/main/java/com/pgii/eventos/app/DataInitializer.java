package com.pgii.eventos.app;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import com.pgii.eventos.patterns.creational.factory.EventoFactory;

public class DataInitializer {

    public static void inicializar(RecintoRepository recintoRepo,
                                   ZonaRepository zonaRepo,
                                   AsientoRepository asientoRepo,
                                   EventoRepository eventoRepo,
                                   UsuarioRepository usuarioRepo,
                                   AdministradorRepository adminRepo,
                                   CompraRepository compraRepo) {

        // ========== RECINTOS, ZONAS, ASIENTOS ==========
        Recinto recinto1 = new Recinto("R001", "Estadio Centenario", "Calle 30 # 20-10", "Armenia");
        Recinto recinto2 = new Recinto("R002", "Teatro Azul", "Carrera 14 # 12-45", "Armenia");

        // Zonas para recinto1
        Zona vip = new Zona("Z001", "VIP", 50, 250_000.0, recinto1);
        Zona preferencial = new Zona("Z002", "Preferencial", 100, 150_000.0, recinto1);
        Zona general = new Zona("Z003", "General", 200, 80_000.0, recinto1);

        // Zonas para recinto2 (teatro, asientos numerados)
        Zona platea = new Zona("Z004", "Platea", 80, 120_000.0, recinto2);
        Zona balcon = new Zona("Z005", "Balcón", 60, 90_000.0, recinto2);

        recinto1.agregarZona(vip);
        recinto1.agregarZona(preferencial);
        recinto1.agregarZona(general);
        recinto2.agregarZona(platea);
        recinto2.agregarZona(balcon);

        // Guardar recintos (esto guarda las zonas también por referencia, pero las zonas se guardan aparte)
        recintoRepo.save(recinto1);
        recintoRepo.save(recinto2);

        // Guardar zonas (necesario para que los repositorios tengan las zonas)
        zonaRepo.save(vip);
        zonaRepo.save(preferencial);
        zonaRepo.save(general);
        zonaRepo.save(platea);
        zonaRepo.save(balcon);

        // Crear asientos para las zonas
        // Para VIP: filas A y B, asientos 1 al 5
        for (char fila = 'A'; fila <= 'B'; fila++) {
            for (int num = 1; num <= 5; num++) {
                String idAsiento = "ASI_" + vip.getIdZona() + "_" + fila + num;
                Asiento asiento = new Asiento(idAsiento, String.valueOf(fila), num, vip);
                vip.agregarAsiento(asiento);
                asientoRepo.save(asiento);
            }
        }
        // Para Preferencial: filas C,D,E asientos 1-8
        for (char fila = 'C'; fila <= 'E'; fila++) {
            for (int num = 1; num <= 8; num++) {
                String idAsiento = "ASI_" + preferencial.getIdZona() + "_" + fila + num;
                Asiento asiento = new Asiento(idAsiento, String.valueOf(fila), num, preferencial);
                preferencial.agregarAsiento(asiento);
                asientoRepo.save(asiento);
            }
        }
        // Para General: no se crean asientos numerados (se deja null en Entrada)
        // Para Platea: asientos F1 a F15
        for (int num = 1; num <= 15; num++) {
            String idAsiento = "ASI_" + platea.getIdZona() + "_F" + num;
            Asiento asiento = new Asiento(idAsiento, "F", num, platea);
            platea.agregarAsiento(asiento);
            asientoRepo.save(asiento);
        }
        // Para Balcón: asientos G1 a G10
        for (int num = 1; num <= 10; num++) {
            String idAsiento = "ASI_" + balcon.getIdZona() + "_G" + num;
            Asiento asiento = new Asiento(idAsiento, "G", num, balcon);
            balcon.agregarAsiento(asiento);
            asientoRepo.save(asiento);
        }

        // ========== EVENTOS ==========
        Evento evento1 = EventoFactory.crearEvento("E001", "Ferxxo en concierto", CategoriaEvento.CONCIERTO,
                "Concierto de rock", "Armenia", LocalDateTime.of(2025, 6, 15, 20, 0), recinto1);
        evento1.setEstado(EstadoEvento.PUBLICADO);

        Evento evento2 = new Evento("E002", "Hamlet - Teatro", CategoriaEvento.TEATRO,
                "Obra de Shakespeare", "Armenia", LocalDateTime.of(2025, 7, 10, 19, 30), recinto2);
        evento2.setEstado(EstadoEvento.PUBLICADO);

        Evento evento3 = new Evento("E003", "Conferencia Java", CategoriaEvento.CONFERENCIA,
                "Evento de tecnología", "Armenia", LocalDateTime.of(2025, 5, 20, 9, 0), recinto1);
        evento3.setEstado(EstadoEvento.PAUSADO);  // pausado para probar

        Evento evento4 = new Evento("E004", "Festival de Jazz", CategoriaEvento.CONCIERTO,
                "Música en vivo", "Armenia", LocalDateTime.of(2025, 4, 1, 18, 0), recinto1);
        evento4.setEstado(EstadoEvento.CANCELADO);

        eventoRepo.save(evento1);
        eventoRepo.save(evento2);
        eventoRepo.save(evento3);
        eventoRepo.save(evento4);

        // ========== USUARIOS Y ADMINISTRADORES ==========
        Usuario user1 = new Usuario("U001", "Juan Pérez", "juan@mail.com", "300111222");
        user1.agregarMetodoPago("Tarjeta crédito ****1234");
        user1.agregarMetodoPago("PSE");

        Usuario user2 = new Usuario("U002", "María Gómez", "maria@mail.com", "310333444");
        user2.agregarMetodoPago("Tarjeta débito ****5678");

        Administrador admin = new Administrador("AD001", "Admin Principal", "admin@eventos.com", "600555666");

        usuarioRepo.save(user1);
        usuarioRepo.save(user2);
        adminRepo.save(admin);

        // ========== COMPRAS PRECARGADAS ==========
        // Compra 1: Pagada (para user1, evento1, entrada VIP con asiento)
        Compra compra1 = new Compra("C001", user1, evento1);
        // Buscar un asiento VIP disponible (por ejemplo el primero de la fila A)
        Asiento asientoVIP = vip.getAsientos().stream().findFirst().orElse(null);
        Entrada entrada1 = new Entrada("ENT001", evento1, vip, asientoVIP, vip.getPrecioBase());
        compra1.agregarItem(entrada1);
        compra1.setEstado(EstadoCompra.PAGADA);
        Pago pago1 = new Pago("P001", "Tarjeta crédito", compra1.getTotal(), LocalDateTime.now().minusDays(2));
        pago1.setEstado(EstadoPago.APROBADO);
        compra1.setPago(pago1);
        user1.agregarCompra(compra1);
        compraRepo.save(compra1);
        // Marcar asiento como vendido
        if (asientoVIP != null) asientoVIP.setEstado(EstadoAsiento.VENDIDO);

        // Compra 2: Cancelada (user2, evento2, entrada platea)
        Compra compra2 = new Compra("C002", user2, evento2);
        // Obtener un asiento de platea
        Asiento asientoPlatea = platea.getAsientos().stream().findFirst().orElse(null);
        Entrada entrada2 = new Entrada("ENT002", evento2, platea, asientoPlatea, platea.getPrecioBase());
        compra2.agregarItem(entrada2);
        compra2.setEstado(EstadoCompra.CANCELADA);
        // No tiene pago asociado
        user2.agregarCompra(compra2);
        compraRepo.save(compra2);
        // El asiento debe quedar disponible (no se cambia estado porque la compra se canceló antes de pagar)
        // Para simular, lo dejamos como estaba (DISPONIBLE)

        // Compra 3: En estado CREADA (sin pagar) para user1, evento2
        Compra compra3 = new Compra("C003", user1, evento2);
        Asiento asientoBalcon = balcon.getAsientos().stream().findFirst().orElse(null);
        Entrada entrada3 = new Entrada("ENT003", evento2, balcon, asientoBalcon, balcon.getPrecioBase());
        compra3.agregarItem(entrada3);
        compra3.setEstado(EstadoCompra.CREADA);
        user1.agregarCompra(compra3);
        compraRepo.save(compra3);
        // asiento reservado? se marcaría como RESERVADO, pero en este modelo no lo hacemos aún
        // Lo dejamos disponible para efectos de prueba
    }

}
