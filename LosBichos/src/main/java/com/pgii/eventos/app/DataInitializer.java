package com.pgii.eventos.app;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.creational.factory.EventoFactory;
import com.pgii.eventos.patterns.structural.decorator.*;
import com.pgii.eventos.repository.*;

import java.time.LocalDateTime;
import java.util.Arrays;

public class DataInitializer {

    public static void inicializar(RecintoRepository recintoRepo,
                                   ZonaRepository zonaRepo,
                                   AsientoRepository asientoRepo,
                                   EventoRepository eventoRepo,
                                   UsuarioRepository usuarioRepo,
                                   AdministradorRepository adminRepo,
                                   CompraRepository compraRepo) {

        Recinto estadio = new Recinto("R001", "Estadio Centenario", "Calle 30 #20-10", "Armenia");
        Recinto teatro = new Recinto("R002", "Teatro Azul", "Carrera 14 #12-45", "Armenia");
        Recinto auditorio = new Recinto("R003", "Auditorio Uniquindío", "Calle 23 #15-30", "Armenia");

        recintoRepo.save(estadio);
        recintoRepo.save(teatro);
        recintoRepo.save(auditorio);

        Zona vipEstadio = new Zona("Z001", "VIP", 80, 280_000.0, estadio);
        Zona preferencialEstadio = new Zona("Z002", "Preferencial", 200, 150_000.0, estadio);
        Zona generalEstadio = new Zona("Z003", "General", 500, 60_000.0, estadio);

        for (char fila = 'A'; fila <= 'D'; fila++) {
            for (int num = 1; num <= 10; num++) {
                String id = "ASI_VIP_" + fila + num;
                Asiento a = new Asiento(id, String.valueOf(fila), num, vipEstadio);
                vipEstadio.agregarAsiento(a);
                asientoRepo.save(a);
            }
        }

        for (char fila = 'E'; fila <= 'J'; fila++) {
            for (int num = 1; num <= 20; num++) {
                String id = "ASI_PREF_" + fila + num;
                Asiento a = new Asiento(id, String.valueOf(fila), num, preferencialEstadio);
                preferencialEstadio.agregarAsiento(a);
                asientoRepo.save(a);
            }
        }

        generalEstadio.setAsientos(Arrays.asList()); // vacío

        estadio.agregarZona(vipEstadio);
        estadio.agregarZona(preferencialEstadio);
        estadio.agregarZona(generalEstadio);
        zonaRepo.save(vipEstadio);
        zonaRepo.save(preferencialEstadio);
        zonaRepo.save(generalEstadio);

        Zona platea = new Zona("Z004", "Platea", 120, 110_000.0, teatro);
        Zona balcon = new Zona("Z005", "Balcón", 80, 70_000.0, teatro);
        for (int i = 1; i <= 120; i++) {
            Asiento a = new Asiento("ASI_PLA_" + i, "P", i, platea);
            platea.agregarAsiento(a);
            asientoRepo.save(a);
        }
        for (int i = 1; i <= 80; i++) {
            Asiento a = new Asiento("ASI_BAL_" + i, "B", i, balcon);
            balcon.agregarAsiento(a);
            asientoRepo.save(a);
        }
        teatro.agregarZona(platea);
        teatro.agregarZona(balcon);
        zonaRepo.save(platea);
        zonaRepo.save(balcon);

        Zona unica = new Zona("Z006", "Única", 300, 40_000.0, auditorio);
        auditorio.agregarZona(unica);
        zonaRepo.save(unica);

        Evento feid = EventoFactory.crearEvento("E001", "Feid - Ferxxo en Armenia", CategoriaEvento.CONCIERTO,
                "El artista del momento en concierto", "Armenia",
                LocalDateTime.of(2025, 7, 20, 20, 0), estadio);
        feid.setEstado(EstadoEvento.PUBLICADO);
        eventoRepo.save(feid);

        Evento hamlet = new Evento("E002", "Hamlet - Teatro", CategoriaEvento.TEATRO,
                "Obra de Shakespeare", "Armenia", LocalDateTime.of(2025, 8, 15, 19, 30), teatro);
        hamlet.setEstado(EstadoEvento.PUBLICADO);
        eventoRepo.save(hamlet);

        Evento javaConf = new Evento("E003", "Conferencia Java 2025", CategoriaEvento.CONFERENCIA,
                "Evento de tecnología con speakers internacionales", "Armenia",
                LocalDateTime.of(2025, 9, 10, 9, 0), auditorio);
        javaConf.setEstado(EstadoEvento.PUBLICADO);
        eventoRepo.save(javaConf);

        Evento jazz = new Evento("E004", "Festival de Jazz", CategoriaEvento.CONCIERTO,
                "Música en vivo", "Armenia", LocalDateTime.of(2025, 4, 1, 18, 0), estadio);
        jazz.setEstado(EstadoEvento.CANCELADO);
        eventoRepo.save(jazz);

        Evento rock = new Evento("E005", "Rock al parque", CategoriaEvento.CONCIERTO,
                "Bandas locales", "Armenia", LocalDateTime.of(2025, 10, 5, 15, 0), estadio);
        rock.setEstado(EstadoEvento.PAUSADO);
        eventoRepo.save(rock);

        Usuario user1 = new Usuario("U001", "Juan Pérez", "juan@mail.com", "300111222");
        user1.agregarMetodoPago("Tarjeta crédito ****1234");
        user1.agregarMetodoPago("PSE");

        Usuario user2 = new Usuario("U002", "María Gómez", "maria@mail.com", "310333444");
        user2.agregarMetodoPago("Tarjeta débito ****5678");

        Usuario user3 = new Usuario("U003", "Carlos López", "carlos@mail.com", "320555666");
        user3.agregarMetodoPago("Efectivo");

        Administrador admin = new Administrador("AD001", "Admin Principal", "admin@eventos.com", "600555666");
        adminRepo.save(admin);
        System.out.println("=== ADMIN CREADO ===");
        System.out.println("Email: " + admin.getEmail());
        System.out.println("Total admins en repo: " + adminRepo.findAll().size());

        usuarioRepo.save(user1);
        usuarioRepo.save(user2);
        usuarioRepo.save(user3);


        java.util.function.BiFunction<Evento, Zona, ItemCompra> crearEntradaBase = (ev, zona) -> {
            Asiento asiento = zona.getAsientos().stream().findFirst().orElse(null);
            return new Entrada("ENT_" + System.currentTimeMillis() + "_" + zona.getIdZona(),
                    ev, zona, asiento, zona.getPrecioBase());
        };

        Compra c1 = new Compra("C001", user1, feid);
        ItemCompra entradaFeid = crearEntradaBase.apply(feid, vipEstadio);
        ItemCompra vipDecorada = new VIPDecorator(entradaFeid);
        ItemCompra vipSeguro = new SeguroDecorator(vipDecorada);
        c1.agregarItem(vipSeguro);
        c1.setEstado(EstadoCompra.PAGADA);
        Pago p1 = new Pago("P001", "Tarjeta crédito", c1.getTotal(), LocalDateTime.now().minusDays(1));
        p1.setEstado(EstadoPago.APROBADO);
        c1.setPago(p1);
        user1.agregarCompra(c1);
        compraRepo.save(c1);

        vipEstadio.getAsientos().stream().findFirst().ifPresent(a -> a.setEstado(EstadoAsiento.VENDIDO));

        Compra c2 = new Compra("C002", user2, feid);
        ItemCompra entradaPref = crearEntradaBase.apply(feid, preferencialEstadio);
        ItemCompra prefMerch = new MerchandisingDecorator(entradaPref);
        ItemCompra prefMerchPark = new ParqueaderoDecorator(prefMerch);
        c2.agregarItem(prefMerchPark);
        c2.setEstado(EstadoCompra.PAGADA);
        Pago p2 = new Pago("P002", "PSE", c2.getTotal(), LocalDateTime.now().minusHours(3));
        p2.setEstado(EstadoPago.APROBADO);
        c2.setPago(p2);
        user2.agregarCompra(c2);
        compraRepo.save(c2);
        preferencialEstadio.getAsientos().stream().findFirst().ifPresent(a -> a.setEstado(EstadoAsiento.VENDIDO));

        Compra c3 = new Compra("C003", user1, hamlet);
        ItemCompra entradaHamlet = crearEntradaBase.apply(hamlet, platea);
        c3.agregarItem(entradaHamlet);
        c3.setEstado(EstadoCompra.PAGADA);
        Pago p3 = new Pago("P003", "Tarjeta débito", c3.getTotal(), LocalDateTime.now().minusDays(2));
        p3.setEstado(EstadoPago.APROBADO);
        c3.setPago(p3);
        user1.agregarCompra(c3);
        compraRepo.save(c3);
        platea.getAsientos().stream().findFirst().ifPresent(a -> a.setEstado(EstadoAsiento.VENDIDO));

        Compra c4 = new Compra("C004", user3, javaConf);
        ItemCompra entradaConf = crearEntradaBase.apply(javaConf, unica);
        c4.agregarItem(entradaConf);
        c4.setEstado(EstadoCompra.CREADA);
        user3.agregarCompra(c4);
        compraRepo.save(c4);

        Compra c5 = new Compra("C005", user2, feid);
        ItemCompra entradaGeneral = crearEntradaBase.apply(feid, generalEstadio);
        c5.agregarItem(entradaGeneral);
        c5.setEstado(EstadoCompra.CANCELADA);
        user2.agregarCompra(c5);
        compraRepo.save(c5);

    }
}
