package org.gestion.dao.jdbc;

import org.gestion.dao.ClienteDAO;
import org.gestion.db.ConnectionManager;
import org.gestion.model.Cliente;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClienteDAO implements ClienteDAO {

    @Override
    public void add(Cliente c) throws IOException {
        String sql = "INSERT INTO clientes (nombre, direccion) VALUES (?, ?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getInt(1));

        } catch (SQLException e) {
            throw new IOException("Error insertando cliente", e);
        }
    }

    @Override
    public void update(Cliente c) throws IOException {
        String sql = "UPDATE clientes SET nombre=?, direccion=? WHERE id=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IOException("Error eliminando cliente", e);
        }
    }

    @Override
    public Optional<Cliente> findById(int id) throws IOException {
        String sql = "SELECT * FROM clientes WHERE id=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            Cliente c = new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("direccion")
            );

            return Optional.of(c);

        } catch (SQLException e) {
            throw new IOException("Error buscando cliente", e);
        }
    }

    @Override
    public List<Cliente> findAll() throws IOException {
        String sql = "SELECT * FROM clientes";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                ));
            }

            return lista;

        } catch (SQLException e) {
            throw new IOException("Error listando clientes", e);
        }
    }
}
