package com.pgii.eventos.model;

import java.util.ArrayList;
import java.util.List;

public class Zona {
    private String idZona;
    private String nombre;
    private int capacidad;
    private double precioBase;
    private Recinto recinto; // asociación inversa (opcional)
    private List<Asiento> asientos; // si maneja asientos numerados

    public Zona(String idZona, String nombre, int capacidad, double precioBase, Recinto recinto) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.recinto = recinto;
        this.asientos = new ArrayList<>();
        // Opcional: inicializar asientos automáticamente según capacidad
    }

    // Getters y setters
    public String getIdZona() { return idZona; }
    public void setIdZona(String idZona) { this.idZona = idZona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public Recinto getRecinto() { return recinto; }
    public void setRecinto(Recinto recinto) { this.recinto = recinto; }

    public List<Asiento> getAsientos() { return asientos; }
    public void setAsientos(List<Asiento> asientos) { this.asientos = asientos; }

    public void agregarAsiento(Asiento asiento) {
        this.asientos.add(asiento);
    }

    // Método para obtener número de asientos disponibles
    public long getAsientosDisponibles() {
        return asientos.stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).count();
    }

    @Override
    public String toString() {
        return nombre + " (Capacidad: " + capacidad + ", Precio base: " + precioBase + ")";
    }
}
