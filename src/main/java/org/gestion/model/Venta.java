package org.gestion.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Venta {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private Map<Integer, Integer> items; // productoId -> cantidad
    private double total;

    public Venta() {}

    public Venta(int id, int clienteId, LocalDateTime fecha, Map<Integer, Integer> items, double total) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.items = new LinkedHashMap<>(items);
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Map<Integer, Integer> getItems() { return Collections.unmodifiableMap(items); }
    public void setItems(Map<Integer, Integer> items) { this.items = new LinkedHashMap<>(items); }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", clienteId=" + clienteId +
                ", items=" + items + ", total=" + total + ", fecha=" + fecha + "}";
    }
}
