package org.gestion.dao.jdbc;

import org.gestion.dao.ProductoDAO;
import org.gestion.model.Producto;
import org.gestion.db.ConnectionManager;

import java.sql.*;
import java.util.*;

public class JdbcProductoDAO implements ProductoDAO {

    @Override
    public void add(Producto p) {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));

        } catch (Exception e) {
            throw new RuntimeException("Error insertando producto", e);
        }
    }

    @Override
    public void update(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setInt(4, p.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando producto", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error borrando producto", e);
        }
    }

    @Override
    public Optional<Producto> findById(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando producto", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Producto> findAll() {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error listando productos", e);
        }

        return list;
    }
}
