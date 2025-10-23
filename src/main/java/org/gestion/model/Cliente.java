package org.gestion.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nombre;
    private String direccion;
    private List<Integer> historialComprasIds = new ArrayList<>(); // ids de ventas

    // constructor, getters y setters
    public Cliente() {
    }
    public Cliente(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Integer> getHistorialComprasIds() {
        return historialComprasIds;
    }

    public void setHistorialComprasIds(List<Integer> historialComprasIds) {
        this.historialComprasIds = historialComprasIds;
    }

    public String getHistorialCompras() {
        StringBuilder sb = new StringBuilder();
        for(int id : historialComprasIds) {
            if(sb.length() > 0) sb.append(";");
            sb.append(id);
        }
        return sb.toString();
    }

    public Cliente setHistorialCompras(String replace) {
        historialComprasIds.clear();
        if(replace == null || replace.isEmpty()) return this;
        String[] parts = replace.split(";");
        for(String part : parts) {
            try {
                historialComprasIds.add(Integer.parseInt(part));
            } catch(NumberFormatException e) {
                // ignorar entradas inválidas
            }
        }
        return this;
    }
    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', direccion='" + direccion +
                "', historialComprasIds=" + historialComprasIds + "}";
    }

}