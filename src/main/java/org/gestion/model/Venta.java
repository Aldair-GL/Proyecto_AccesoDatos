package org.gestion.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Venta {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private Map<Integer, Integer> items; // productoId -> cantidad
    private double total;

    // 🔹 Constructor usado al LEER desde CSV
    public Venta(int id, int clienteId, String fechaStr, double total, String itemsStr) {
        this.id = id;
        this.clienteId = clienteId;
        this.total = total;
        this.items = parseItems(itemsStr);
        this.fecha = parseFecha(fechaStr);
    }

    // 🔹 Constructor usado al CREAR desde el programa
    public Venta(int id, int clienteId, LocalDateTime fecha, Map<Integer, Integer> items, double total) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.items = items;
        this.total = total;
    }

    // 🔹 Método auxiliar: convierte "1:2;2:1" a {1=2, 2=1}
    private Map<Integer, Integer> parseItems(String itemsStr) {
        Map<Integer, Integer> map = new HashMap<>();
        if (itemsStr == null || itemsStr.isEmpty()) return map;
        String[] pares = itemsStr.split(";");
        for (String p : pares) {
            String[] kv = p.split(":");
            if (kv.length == 2) {
                try {
                    int idProd = Integer.parseInt(kv[0].trim());
                    int cant = Integer.parseInt(kv[1].trim());
                    map.put(idProd, cant);
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    // 🔹 Método auxiliar: convierte "2025-10-01T12:30" a LocalDateTime
    private LocalDateTime parseFecha(String f) {
        if (f == null || f.isBlank()) return null;
        try {
            return LocalDateTime.parse(f, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(f, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            } catch (Exception ex) {
                return null;
            }
        }
    }

    // 🔹 Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Map<Integer, Integer> getItems() { return items; }
    public void setItems(Map<Integer, Integer> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // 🔹 Métodos antiguos anulados para compatibilidad
    public Object getIdCliente() { return clienteId; }
    public Object getIdProducto() { return null; }
    public Object getCantidad() { return null; }

    @Override
    public String toString() {
        return "Venta{id=" + id +
                ", clienteId=" + clienteId +
                ", items=" + (items != null ? items.toString() : "{}") +
                ", total=" + total +
                ", fecha=" + (fecha != null ? fecha.toString() : "null") + "}";
    }
}
