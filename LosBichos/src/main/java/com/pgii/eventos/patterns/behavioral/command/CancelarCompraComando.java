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
    }

    @Override
    public void ejecutar() {
        this.estadoAnterior = compra.getEstado();
        compraService.cancelarCompra(compra);
        System.out.println("Comando ejecutado: Cancelar compra " + compra.getIdCompra());
    }

    @Override
    public void deshacer() {
        compra.setEstado(estadoAnterior);
        System.out.println("Undo: Se restauró el estado de la compra a " + estadoAnterior);
    }
}