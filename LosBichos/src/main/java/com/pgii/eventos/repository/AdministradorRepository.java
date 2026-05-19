package com.pgii.eventos.repository;

import com.pgii.eventos.model.Administrador;

public class AdministradorRepository extends InMemoryRepository<Administrador> {
    public AdministradorRepository() {
        super(Administrador::getId);
    }
}