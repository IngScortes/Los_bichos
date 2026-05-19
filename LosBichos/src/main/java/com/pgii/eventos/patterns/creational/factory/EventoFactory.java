package com.pgii.eventos.patterns.creational.factory;

import com.pgii.eventos.model.*;
import java.time.LocalDateTime;

public class EventoFactory {
    public static Evento crearEvento(String id, String nombre, CategoriaEvento categoria,
                                     String descripcion, String ciudad, LocalDateTime fechaHora,
                                     Recinto recinto) {
        Evento evento = new Evento(id, nombre, categoria, descripcion, ciudad, fechaHora, recinto);
        // Asignar políticas según categoría
        switch (categoria) {
            case CONCIERTO:
                evento.setPoliticas("No reembolsable. Cancelación con 48h de anticipación da derecho a 50%.");
                break;
            case TEATRO:
                evento.setPoliticas("Reembolso total hasta 24h antes. Cambios de asiento permitidos.");
                break;
            case CONFERENCIA:
                evento.setPoliticas("Reembolso 100% hasta 7 días antes. Acceso a grabaciones.");
                break;
            default:
                evento.setPoliticas("Políticas generales del evento.");
        }
        return evento;
    }
}
