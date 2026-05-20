package com.pgii.eventos.patterns.behavioral.strategy;

import java.util.UUID;

public class PagoPSE implements MetodoPago {
    private String emailBanco;
    private String password;

    public PagoPSE(String emailBanco, String password) {
        this.emailBanco = emailBanco;
        this.password = password;
    }

    @Override
    public ResultadoPago procesarPago(double monto) {
        if (emailBanco.contains("@") && password.length() >= 4) {
            return new ResultadoPago(true, "Pago PSE exitoso", UUID.randomUUID().toString());
        }
        return new ResultadoPago(false, "Credenciales PSE inválidas", null);
    }
}