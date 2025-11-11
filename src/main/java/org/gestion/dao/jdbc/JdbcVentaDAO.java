package org.gestion.dao.jdbc;

import org.gestion.dao.VentaDAO;
import org.gestion.db.ConnectionManager;
import org.gestion.model.Venta;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class JdbcVentaDAO implements VentaDAO {

    @Override
    public void add(Venta v) throws IOException {
        String sql = "INSERT INTO ventas (cliente_id, fecha, total, items) VALUES (?, ?, ?, ?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, v.getClienteId());
            ps.setString(2, v.getFecha().toString());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, itemsToString(v.getItems()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) v.setId(rs.getInt(1));

        } catch (SQLException e) {
            throw new IOException("Error insertando venta", e);
        }
    }

    @Override
    public void update(Venta v) throws IOException {
        String sql = "UPDATE ventas SET cliente_id=?, fecha=?, total=?, items=? WHERE id=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getClienteId());
            ps.setString(2, v.getFecha().toString());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, itemsToString(v.getItems()));
            ps.setInt(5, v.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IOException("Error actualizando venta", e);
        }
    }

    @Override
    public void delete(int id) throws IOException {
        String sql = "DELETE FROM ventas WHERE id=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IOException("Error eliminando venta", e);
        }
    }

    @Override
    public Optional<Venta> findById(int id) throws IOException {
        String sql = "SELECT * FROM ventas WHERE id=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            return Optional.of(buildVenta(rs));

        } catch (SQLException e) {
            throw new IOException("Error buscando venta", e);
        }
    }

    @Override
    public List<Venta> findAll() throws IOException {
        String sql = "SELECT * FROM ventas";
        List<Venta> lista = new ArrayList<>();

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) lista.add(buildVenta(rs));

            return lista;

        } catch (SQLException e) {
            throw new IOException("Error listando ventas", e);
        }
    }

    // ===== Helpers =====

    private Venta buildVenta(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        int clienteId = rs.getInt("cliente_id");

        // Fecha en DATETIME → LocalDateTime
        LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();

        double total = rs.getDouble("total");

        // Items almacenados como "1:2;5:1;7:3"
        String itemsStr = rs.getString("items");
        Map<Integer, Integer> items = new LinkedHashMap<>();

        if (itemsStr != null && !itemsStr.isBlank()) {
            String[] pares = itemsStr.split(";");
            for (String par : pares) {
                if (!par.contains(":")) continue;
                String[] kv = par.split(":");
                int prodId = Integer.parseInt(kv[0]);
                int cantidad = Integer.parseInt(kv[1]);
                items.put(prodId, cantidad);
            }
        }

        return new Venta(id, clienteId, fecha, items, total);
    }


    private String itemsToString(Map<Integer,Integer> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .reduce((a,b) -> a + ";" + b)
                .orElse("");
    }

    private Map<Integer,Integer> stringToItems(String s) {
        Map<Integer,Integer> map = new LinkedHashMap<>();
        if (s == null || s.isEmpty()) return map;

        for (String part : s.split(";")) {
            String[] kv = part.split(":");
            map.put(Integer.parseInt(kv[0]), Integer.parseInt(kv[1]));
        }
        return map;
    }
}
