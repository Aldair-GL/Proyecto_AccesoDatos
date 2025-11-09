package org.gestion.dao;

import org.gestion.model.Cliente;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    void add(Cliente c) throws IOException;         // asigna id
    void update(Cliente c) throws IOException;
    void delete(int id) throws IOException;
    Optional<Cliente> findById(int id) throws IOException;
    List<Cliente> findAll() throws IOException;
}
