package com.pgii.eventos.repository;

import com.pgii.eventos.model.Usuario;

public class UsuarioRepository extends InMemoryRepository<Usuario> {
    public UsuarioRepository() {
        super(Usuario::getId);
    }
}