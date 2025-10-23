package org.gestion.dao;

import org.gestion.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoDAO {
    Producto add(Producto p) throws Exception;
    boolean delete(int id) throws Exception;
    Producto update(Producto p) throws Exception;
    Optional<Producto> findById(int id) throws Exception;
    List<Producto> findAll() throws Exception;
}
