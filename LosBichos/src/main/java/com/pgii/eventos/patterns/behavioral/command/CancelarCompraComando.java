package com.pgii.eventos.patterns.behavioral.command;

import com.pgii.eventos.model.Compra;
import com.pgii.eventos.model.EstadoCompra;
import com.pgii.eventos.service.CompraService;

public class CancelarCompraComando implements Comando {
    private Compra compra;
    private CompraService compraService;
    private EstadoCompra estadoAnterior;

    public CancelarCompraComando(Compra compra, CompraService compraService) {
        this.compra = compra;
        this.compraService = compraService;
        this.estadoAnterior = compra.getEstado();
    }

    @Override
    public void ejecutar() {
        compraService.cancelarCompra(compra);
    }

    @Override
    public void deshacer() {
        compra.setEstado(estadoAnterior);
    }
}