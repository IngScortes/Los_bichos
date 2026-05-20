package com.pgii.eventos.patterns.structural.adapter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.List;

public class ApachePOICSVAdapter implements IReporteExporter {
    @Override
    public void exportar(List<String[]> filas, String rutaArchivo) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte");
        int rowNum = 0;
        for (String[] fila : filas) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String celda : fila) {
                row.createCell(colNum++).setCellValue(celda);
            }
        }
        try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
            workbook.write(out);
        }
        workbook.close();
    }
}