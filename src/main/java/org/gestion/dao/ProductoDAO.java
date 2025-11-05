package org.gestion.dao;

import org.gestion.model.Producto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductoDAO {
    void add(Producto producto) throws IOException;
    List<Producto> findAll() throws IOException;
    Optional<Producto> findById(int id) throws IOException;
    void update(Producto producto) throws IOException;
    void delete(int id) throws IOException;
}
