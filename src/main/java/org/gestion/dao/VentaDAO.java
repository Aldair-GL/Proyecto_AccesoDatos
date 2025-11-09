package org.gestion.dao;

import org.gestion.model.Venta;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VentaDAO {
    void add(Venta v) throws IOException;           // asigna id
    void update(Venta v) throws IOException;
    void delete(int id) throws IOException;
    Optional<Venta> findById(int id) throws IOException;
    List<Venta> findAll() throws IOException;
}
