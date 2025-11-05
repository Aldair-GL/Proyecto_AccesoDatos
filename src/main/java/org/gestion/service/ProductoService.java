package org.gestion.service;

import org.gestion.dao.file.FileProductoDAO;
import org.gestion.model.Producto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProductoService {
    private final FileProductoDAO dao;

    public ProductoService(FileProductoDAO dao) {
        this.dao = dao;
    }

    public Producto addProducto(String nombre, double precio, int stock) throws IOException {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("Nombre vacío");
        if (precio < 0)
            throw new IllegalArgumentException("Precio negativo");
        if (stock < 0)
            throw new IllegalArgumentException("Stock negativo");

        Producto p = new Producto(0, nombre, precio, stock);
        dao.add(p); // ahora add() no devuelve nada
        return p;   // devolvemos el producto creado manualmente
    }

    public Producto updateProducto(Producto p) throws IOException {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("Nombre vacío");
        if (p.getPrecio() < 0)
            throw new IllegalArgumentException("Precio negativo");
        if (p.getStock() < 0)
            throw new IllegalArgumentException("Stock negativo");

        dao.update(p); // update() ahora tampoco devuelve nada
        return p;
    }

    public boolean deleteProducto(int id) throws IOException {
        dao.delete(id); // ahora delete() es void
        return true;    // asumimos éxito si no lanza excepción
    }

    public Optional<Producto> findById(int id) throws IOException {
        return dao.findById(id);
    }

    public List<Producto> listar() throws IOException {
        return dao.findAll();
    }
}
