package com.pgii.eventos.patterns.behavioral.command;

public interface Comando {
    void ejecutar();
    void deshacer();
}