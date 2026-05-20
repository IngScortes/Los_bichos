package com.pgii.eventos.patterns.behavioral.strategy;

public class ResultadoPago {
    private boolean exitoso;
    private String mensaje;
    private String codigoTransaccion;

    public ResultadoPago(boolean exitoso, String mensaje, String codigoTransaccion) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.codigoTransaccion = codigoTransaccion;
    }

    public boolean isExitoso() { return exitoso; }
    public String getMensaje() { return mensaje; }
    public String getCodigoTransaccion() { return codigoTransaccion; }
}
