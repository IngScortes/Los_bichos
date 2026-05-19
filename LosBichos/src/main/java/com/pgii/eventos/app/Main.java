package com.pgii.eventos.app;

import com.pgii.eventos.model.*;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Crear recinto
        Recinto recinto = new Recinto("R001", "Estadio Centenario", "Calle 1", "Armenia");
        Zona vip = new Zona("Z001", "VIP", 50, 250_000, recinto);
        recinto.agregarZona(vip);

        // Crear evento
        Evento concierto = new Evento("E001", "Ferxxo en concierto", CategoriaEvento.CONCIERTO,
                "Concierto de rock", "Armenia", LocalDateTime.of(2025, 6, 15, 20, 0), recinto);
        concierto.setEstado(EstadoEvento.PUBLICADO);

        // Crear usuario
        Usuario usuario = new Usuario("U001", "Juan Pérez", "juan@mail.com", "3001234567");
        usuario.agregarMetodoPago("Tarjeta crédito ****1234");

        // Crear asiento y entrada
        Asiento asiento = new Asiento("A001", "A", 1, vip);
        vip.agregarAsiento(asiento);
        Entrada entrada = new Entrada("ENT001", concierto, vip, asiento, vip.getPrecioBase());

        // Crear compra
        Compra compra = new Compra("C001", usuario, concierto);
        compra.agregarItem(entrada);
        usuario.agregarCompra(compra);

        System.out.println("Usuario: " + usuario.getNombreCompleto());
        System.out.println("Compra: " + compra);
        System.out.println("Items de la compra: " + compra.getItems().size());
        for (ItemCompra item : compra.getItems()) {
            System.out.println(" - " + item.getDescripcion() + " => $" + item.getPrecio());
        }
    }
}