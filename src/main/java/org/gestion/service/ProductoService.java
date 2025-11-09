package org.gestion.service;

import org.gestion.dao.ProductoDAO;
import org.gestion.model.Producto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProductoService {
    private final ProductoDAO dao;

    public ProductoService(ProductoDAO dao) {
        this.dao = dao;
    }

    public Producto add(String nombre, double precio, int stock) throws IOException {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (precio < 0) throw new IllegalArgumentException("Precio negativo");
        if (stock < 0) throw new IllegalArgumentException("Stock negativo");
        Producto p = new Producto(0, nombre, precio, stock);
        dao.add(p);
        return p;
    }

    public Producto update(Producto p) throws IOException {
        if (p.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        if (p.getNombre() == null || p.getNombre().isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (p.getPrecio() < 0) throw new IllegalArgumentException("Precio negativo");
        if (p.getStock() < 0) throw new IllegalArgumentException("Stock negativo");
        dao.update(p);
        return p;
    }

    public void delete(int id) throws IOException { dao.delete(id); }

    public Optional<Producto> findById(int id) throws IOException { return dao.findById(id); }

    public List<Producto> list() throws IOException { return dao.findAll(); }
}
