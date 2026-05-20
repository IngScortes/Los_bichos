package com.pgii.eventos.patterns.behavioral.observer;

public interface Observador {
    void notificar(String evento, String mensaje, Object fuente);
}
