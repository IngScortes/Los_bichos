package com.pgii.eventos.patterns.structural.composite;

import java.util.List;

public interface ComponenteRecinto {
    int getCapacidadTotal();
    int getOcupacionActual(); // cuenta asientos VENDIDOS o RESERVADOS según regla
    String getNombre();
    List<ComponenteRecinto> getHijos(); // para composite, hoja retorna lista vacía
}