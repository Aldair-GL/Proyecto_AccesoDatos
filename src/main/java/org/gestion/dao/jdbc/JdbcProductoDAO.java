package org.gestion.dao.jdbc;

import org.gestion.dao.ProductoDAO;
import org.gestion.model.Producto;
import org.gestion.db.ConnectionManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductoDAO implements ProductoDAO {

    @Override
    public void add(Producto p) throws IOException {
        String sql = "INSERT INTO productos (id, nombre, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId()); // usar id del CSV
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error insertando producto", e);
        }
    }

    @Override
    public void update(Producto p) throws IOException {
        String sql = "UPDATE productos SET nombre=?, precio=?, stock=? WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error actualizando producto", e);
        }
    }

    @Override
    public void delete(int id) throws IOException {
        String sql = "DELETE FROM productos WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error eliminando producto", e);
        }
    }

    @Override
    public Optional<Producto> findById(int id) throws IOException {
        String sql = "SELECT id, nombre, precio, stock FROM productos WHERE id=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Producto p = new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock"));
                return Optional.of(p);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IOException("Error buscando producto", e);
        }
    }

    @Override
    public List<Producto> findAll() throws IOException {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, stock FROM productos";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            throw new IOException("Error listando productos", e);
        }
        return list;
    }
}
