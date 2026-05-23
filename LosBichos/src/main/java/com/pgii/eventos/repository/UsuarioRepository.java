package com.pgii.eventos.repository;

import com.pgii.eventos.model.Usuario;

public class UsuarioRepository extends InMemoryRepository<Usuario> {
    private static UsuarioRepository instance;

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    private UsuarioRepository() {
        super(Usuario::getId);
    }
}