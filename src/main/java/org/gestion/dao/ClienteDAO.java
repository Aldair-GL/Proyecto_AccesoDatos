package org.gestion.dao;

import org.gestion.model.Cliente;
import org.gestion.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    Cliente add(Cliente c) throws Exception;
    boolean delete(int id) throws Exception;
    Cliente update(Cliente c) throws Exception;
    Optional<Cliente> findById(int id) throws Exception;
    List<Cliente> findAll() throws Exception;
}
