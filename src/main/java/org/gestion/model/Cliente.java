package org.gestion.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nombre;
    private String direccion;
    private List<Integer> historialComprasIds = new ArrayList<>();

    public Cliente() {}

    public Cliente(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public List<Integer> getHistorialComprasIds() { return historialComprasIds; }
    public void setHistorialComprasIds(List<Integer> historialComprasIds) { this.historialComprasIds = historialComprasIds; }

    // CSV helpers
    public String getHistorialComprasCsv() {
        if (historialComprasIds == null || historialComprasIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Integer id : historialComprasIds) {
            if (sb.length() > 0) sb.append(";");
            sb.append(id);
        }
        return sb.toString();
    }

    public void setHistorialFromCsv(String csv) {
        historialComprasIds.clear();
        if (csv == null || csv.isBlank()) return;
        String[] parts = csv.split(";");
        for (String p : parts) {
            try { historialComprasIds.add(Integer.parseInt(p.trim())); }
            catch (NumberFormatException ignored) {}
        }
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', direccion='" + direccion +
                "', historialVentas=" + historialComprasIds + "}";
    }
}
