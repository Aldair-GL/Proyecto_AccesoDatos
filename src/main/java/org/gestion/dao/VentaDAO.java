package org.gestion.dao;

import org.gestion.model.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaDAO {
    Venta add(Venta v) throws Exception;
    boolean delete(int id) throws Exception;
    Venta update(Venta v) throws Exception;
    Optional<Venta> findById(int id) throws Exception;
    List<Venta> findAll() throws Exception;
}
