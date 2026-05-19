package com.pgii.eventos.service;

import com.pgii.eventos.model.Persona;

public class GestorSesion {
    private static GestorSesion instancia;
    private Persona usuarioActivo;

    private GestorSesion() {}

    public static GestorSesion getInstance() {
        if (instancia == null) {
            instancia = new GestorSesion();
        }
        return instancia;
    }

    public Persona getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Persona usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }

    public void cerrarSesion() {
        this.usuarioActivo = null;
    }

    public boolean isAdmin() {
        return usuarioActivo instanceof com.pgii.eventos.model.Administrador;
    }
}