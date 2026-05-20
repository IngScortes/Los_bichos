package com.pgii.eventos.patterns.behavioral.command;

import java.util.Stack;

public class InvocadorComandos {
    private Stack<Comando> historial = new Stack<>();

    public void ejecutar(Comando cmd) {
        cmd.ejecutar();
        historial.push(cmd);
    }

    public void deshacer() {
        if (!historial.isEmpty()) {
            Comando cmd = historial.pop();
            cmd.deshacer();
        } else {
            System.out.println("No hay comandos para deshacer.");
        }
    }
}
