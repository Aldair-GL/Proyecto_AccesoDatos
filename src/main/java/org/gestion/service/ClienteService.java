package org.gestion.service;

import org.gestion.dao.file.FileClienteDAO;
import org.gestion.model.Cliente;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final FileClienteDAO dao;

    public ClienteService(FileClienteDAO dao) {
        this.dao = dao;
    }

    public Cliente addCliente(String nombre, String direccion) throws IOException {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (direccion == null) direccion = "";
        Cliente c = new Cliente(0, nombre, direccion);
        return dao.add(c);
    }

    public Cliente updateCliente(Cliente c) throws IOException {
        if (c.getNombre() == null || c.getNombre().isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (c.getDireccion() == null) c.setDireccion("");
        return dao.update(c);
    }

    public boolean deleteCliente(int id) throws IOException {
        return dao.delete(id);
    }

    public Optional<Cliente> findById(int id) throws IOException {
        return dao.findById(id);
    }

    public List<Cliente> listar() throws IOException {
        return dao.findAll();
    }
}
