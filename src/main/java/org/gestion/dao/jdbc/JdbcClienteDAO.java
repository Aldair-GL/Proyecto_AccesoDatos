package org.gestion.dao.jdbc;

import org.gestion.dao.ClienteDAO;
import org.gestion.model.Cliente;
import org.gestion.db.ConnectionManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClienteDAO implements ClienteDAO {

    @Override
    public void add(Cliente c) throws IOException {
        String sql = "INSERT INTO clientes (id, nombre, direccion) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getId()); // usar el id del CSV
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDireccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error insertando cliente", e);
        }
    }

    @Override
    public void update(Cliente c) throws IOException {
        String sql = "UPDATE clientes SET nombre=?, direccion=? WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error actualizando cliente", e);
        }
    }

    @Override
    public void delete(int id) throws IOException {
        String sql = "DELETE FROM clientes WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error eliminando cliente", e);
        }
    }

    @Override
    public Optional<Cliente> findById(int id) throws IOException {
        String sql = "SELECT id, nombre, direccion FROM clientes WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                return Optional.of(c);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IOException("Error buscando cliente", e);
        }
    }

    @Override
    public List<Cliente> findAll() throws IOException {
        List<Cliente> list = new ArrayList<>();
        String sql = "SELECT id, nombre, direccion FROM clientes";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion")));
            }
        } catch (SQLException e) {
            throw new IOException("Error listando clientes", e);
        }
        return list;
    }
}
