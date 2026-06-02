package com.pgii.eventos.service;

import com.pgii.eventos.model.Incidencia;
import com.pgii.eventos.repository.IncidenciaRepository;
import java.util.List;

public class IncidenciaService {
    private IncidenciaRepository incidenciaRepo;

    public IncidenciaService(IncidenciaRepository incidenciaRepo) {
        this.incidenciaRepo = incidenciaRepo;
    }

    public void registrar(Incidencia incidencia) {
        incidenciaRepo.save(incidencia);
        System.out.println("⚠️ Incidencia registrada: " + incidencia.getTitulo());
    }

    public List<Incidencia> listarTodas() {
        return incidenciaRepo.findAll();
    }

    public void avanzarEstado(String id) {
        Incidencia i = incidenciaRepo.findById(id);
        if (i != null) {
            i.avanzarEstado();
            incidenciaRepo.save(i);
        }
    }
}