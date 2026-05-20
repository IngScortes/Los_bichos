package com.pgii.eventos.model;

import com.pgii.eventos.patterns.structural.composite.ComponenteRecinto;

import java.util.ArrayList;
import java.util.List;

public class Recinto implements ComponenteRecinto {
    private String idRecinto;
    private String nombre;
    private String direccion;
    private String ciudad;
    private List<Zona> zonas;

    public Recinto(String idRecinto, String nombre, String direccion, String ciudad) {
        this.idRecinto = idRecinto;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = new ArrayList<>();
    }

    // Getters y setters
    public String getIdRecinto() { return idRecinto; }
    public void setIdRecinto(String idRecinto) { this.idRecinto = idRecinto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public List<Zona> getZonas() { return zonas; }
    public void setZonas(List<Zona> zonas) { this.zonas = zonas; }

    public void agregarZona(Zona zona) {
        this.zonas.add(zona);
    }

    public void eliminarZona(Zona zona) {
        this.zonas.remove(zona);
    }

    @Override
    public String toString() {
        return nombre + " (" + ciudad + ")";
    }
    @Override
    public int getCapacidadTotal() {
        return zonas.stream().mapToInt(ComponenteRecinto::getCapacidadTotal).sum();
    }

    @Override
    public int getOcupacionActual() {
        return zonas.stream().mapToInt(ComponenteRecinto::getOcupacionActual).sum();
    }

    @Override
    public List<ComponenteRecinto> getHijos() {
        return new ArrayList<>(zonas);
    }
}