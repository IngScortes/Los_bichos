package com.pgii.eventos.patterns.behavioral.strategy;

import java.util.UUID;

public class PagoTarjetaCredito implements MetodoPago {
    private String numeroTarjeta;
    private String cvv;

    public PagoTarjetaCredito(String numeroTarjeta, String cvv) {
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
    }

    @Override
    public ResultadoPago procesarPago(double monto) {
        // Simulación de validación
        if (numeroTarjeta.length() >= 13 && cvv.length() == 3) {
            return new ResultadoPago(true, "Pago con tarjeta de crédito aprobado", UUID.randomUUID().toString());
        }
        return new ResultadoPago(false, "Tarjeta inválida", null);
    }
}
