package com.pgii.eventos.app;

import com.pgii.eventos.model.Usuario;

public class Main {
    public static void main(String[] args) {
        Usuario usuario = new Usuario("U001", "Juan Pérez", "juan@mail.com", "3001234567");
        usuario.agregarMetodoPago("Tarjeta crédito ****1234");
        System.out.println("Usuario creado: " + usuario);
        System.out.println("Métodos de pago: " + usuario.getMetodosPago());
    }
}