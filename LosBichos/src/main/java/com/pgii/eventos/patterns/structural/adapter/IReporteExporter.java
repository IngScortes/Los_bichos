package com.pgii.eventos.patterns.structural.adapter;

import java.util.List;

public interface IReporteExporter {
    void exportar(List<String[]> filas, String rutaArchivo) throws Exception;
}
