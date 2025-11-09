package org.gestion.service;

import org.gestion.dao.ClienteDAO;
import org.gestion.dao.ProductoDAO;
import org.gestion.dao.VentaDAO;
import org.gestion.model.Cliente;
import org.gestion.model.Producto;
import org.gestion.model.Venta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class VentaService {
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;
    private final VentaDAO ventaDAO;

    public VentaService(ProductoDAO productoDAO, ClienteDAO clienteDAO, VentaDAO ventaDAO) {
        this.productoDAO = productoDAO;
        this.clienteDAO = clienteDAO;
        this.ventaDAO = ventaDAO;
    }

    public Venta registrarVenta(int clienteId, Map<Integer, Integer> items) throws IOException {
        // Validaciones
        Cliente cliente = clienteDAO.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe: " + clienteId));
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Sin items");

        // Cargar productos y verificar stock
        Map<Integer, Producto> prods = new LinkedHashMap<>();
        double total = 0.0;

        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            int pid = e.getKey();
            int qty = e.getValue();
            Producto p = productoDAO.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + pid));
            if (qty <= 0) throw new IllegalArgumentException("Cantidad inválida para producto " + pid);
            if (p.getStock() < qty) throw new IllegalArgumentException("Stock insuficiente para " + p.getNombre());
            prods.put(pid, p);
            total += p.getPrecio() * qty;
        }

        // Descontar stock y persistir productos
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            Producto p = prods.get(e.getKey());
            p.setStock(p.getStock() - e.getValue());
            productoDAO.update(p);
        }

        // Crear y guardar venta
        Venta v = new Venta(0, clienteId, LocalDateTime.now(), items, total);
        ventaDAO.add(v); // asigna ID dentro

        // Actualizar historial del cliente
        cliente.getHistorialComprasIds().add(v.getId());
        clienteDAO.update(cliente);

        return v;
    }

    public void eliminarVenta(int ventaId) throws IOException {
        Venta v = ventaDAO.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no existe: " + ventaId));

        // Restaurar stock
        for (Map.Entry<Integer, Integer> e : v.getItems().entrySet()) {
            Producto p = productoDAO.findById(e.getKey())
                    .orElseThrow(() -> new IllegalStateException("Producto perdido: " + e.getKey()));
            p.setStock(p.getStock() + e.getValue());
            productoDAO.update(p);
        }

        // Quitar del historial del cliente
        Cliente c = clienteDAO.findById(v.getClienteId())
                .orElseThrow(() -> new IllegalStateException("Cliente perdido: " + v.getClienteId()));
        c.getHistorialComprasIds().removeIf(id -> id == v.getId());
        clienteDAO.update(c);

        // Borrar venta
        ventaDAO.delete(ventaId);
    }

    public List<Venta> listar() throws IOException { return ventaDAO.findAll(); }
}
