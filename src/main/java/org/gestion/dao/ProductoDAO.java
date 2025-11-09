package org.gestion.dao;

import org.gestion.model.Producto;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductoDAO {
    void add(Producto p) throws IOException;        // asigna id
    void update(Producto p) throws IOException;
    void delete(int id) throws IOException;
    Optional<Producto> findById(int id) throws IOException;
    List<Producto> findAll() throws IOException;
}
