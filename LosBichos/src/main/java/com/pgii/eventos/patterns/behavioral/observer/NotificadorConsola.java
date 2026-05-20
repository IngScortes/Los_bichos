package com.pgii.eventos.patterns.behavioral.observer;

public class NotificadorConsola implements Observador {
    @Override
    public void notificar(String evento, String mensaje, Object fuente) {
        System.out.println("[NOTIFICACIÓN] " + evento + " - " + mensaje + " (Fuente: " + fuente.getClass().getSimpleName() + ")");
    }
}