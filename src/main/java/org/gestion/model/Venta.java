package org.gestion.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Venta {
    private int id;
    private int clienteId;
    private LocalDateTime fecha;
    private Map<Integer, Integer> items; // productoId -> cantidad
    private double total;

    // constructor, getters y setters
    public Venta() {
    }

    public Venta(int id, int clienteId, LocalDateTime fecha, Map<Integer, Integer> items, double total) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.items = items;
        this.total = total;
    }

    public Venta(int id, int idCliente, int idProducto, int cantidad, double total, String fecha) {
        this.id = id;
        this.clienteId = idCliente;
        this.total = total;
        this.fecha = LocalDateTime.parse(fecha);
        this.items = Map.of(idProducto, cantidad);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Object getIdCliente() {
        return clienteId;
    }

    public Object getIdProducto() {
        return null;
    }

    public Object getCantidad() {
        return null;
    }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", clienteId=" + clienteId +
                ", cantidad=" + items + ", total=" + total + ", fecha='" + fecha + "'}";
    }


}
