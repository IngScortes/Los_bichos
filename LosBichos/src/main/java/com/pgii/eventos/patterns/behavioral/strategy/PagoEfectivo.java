package com.pgii.eventos.patterns.behavioral.strategy;

import java.util.UUID;

public class PagoEfectivo implements MetodoPago {
    @Override
    public ResultadoPago procesarPago(double monto) {
        return new ResultadoPago(true, "Pago en efectivo registrado", UUID.randomUUID().toString());
    }
}
