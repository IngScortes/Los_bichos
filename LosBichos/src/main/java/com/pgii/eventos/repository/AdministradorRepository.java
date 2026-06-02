package com.pgii.eventos.repository;

import com.pgii.eventos.model.Administrador;

public class AdministradorRepository extends InMemoryRepository<Administrador> {
    private static AdministradorRepository instance;

    public static AdministradorRepository getInstance() {
        if (instance == null) {
            instance = new AdministradorRepository();
        }
        return instance;
    }

    private AdministradorRepository() {
        super(Administrador::getId);
    }
}