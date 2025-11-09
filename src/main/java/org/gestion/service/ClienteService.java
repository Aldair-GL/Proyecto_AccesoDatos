package org.gestion.service;

import org.gestion.dao.ClienteDAO;
import org.gestion.model.Cliente;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteDAO dao;

    public ClienteService(ClienteDAO dao) { this.dao = dao; }

    public Cliente add(String nombre, String direccion) throws IOException {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (direccion == null) direccion = "";
        Cliente c = new Cliente(0, nombre, direccion);
        dao.add(c);
        return c;
    }

    public Cliente update(Cliente c) throws IOException {
        if (c.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        if (c.getNombre() == null || c.getNombre().isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (c.getDireccion() == null) c.setDireccion("");
        dao.update(c);
        return c;
    }

    public void delete(int id) throws IOException { dao.delete(id); }

    public Optional<Cliente> findById(int id) throws IOException { return dao.findById(id); }

    public List<Cliente> list() throws IOException { return dao.findAll(); }
}
