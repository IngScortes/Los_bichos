package com.pgii.eventos.patterns.behavioral.strategy;

public interface MetodoPago {
    ResultadoPago procesarPago(double monto);
}