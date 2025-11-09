package org.gestion.dao.file;

import org.gestion.dao.VentaDAO;
import org.gestion.model.Venta;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileVentaDAO implements VentaDAO {
    private final Path path;

    public FileVentaDAO(String filepath) {
        this.path = Paths.get(filepath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "id,clienteId,fecha,total,items\n", StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creando ventas.csv", e);
        }
    }

    private Map<Integer, Integer> parseItems(String s) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        if (s == null || s.isBlank()) return map;
        String raw = s.replace("\"", "").trim();
        if (raw.isBlank()) return map;
        String[] pairs = raw.split(";");
        for (String pair : pairs) {
            if (pair.isBlank()) continue;
            String[] kv = pair.split(":");
            if (kv.length != 2) continue;
            try {
                int pid = Integer.parseInt(kv[0].trim());
                int qty = Integer.parseInt(kv[1].trim());
                map.put(pid, qty);
            } catch (NumberFormatException ignored) {}
        }
        return map;
    }

    private String itemsToString(Map<Integer, Integer> items) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(e.getKey()).append(":").append(e.getValue());
        }
        return sb.toString();
    }

    private List<Venta> readAll() throws IOException {
        List<Venta> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id,")) continue;
                // split CSV simple (sin comillas internas)
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    int clienteId = Integer.parseInt(parts[1].trim());
                    LocalDateTime fecha = LocalDateTime.parse(parts[2].trim());
                    double total = Double.parseDouble(parts[3].trim());
                    Map<Integer, Integer> items = parseItems(parts[4].trim());
                    list.add(new Venta(id, clienteId, fecha, items, total));
                } catch (Exception ex) {
                    System.out.println("Fila venta ignorada: " + line);
                }
            }
        }
        return list;
    }

    private void writeAll(List<Venta> ventas) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,clienteId,fecha,total,items\n");
            for (Venta v : ventas) {
                bw.write(v.getId() + "," + v.getClienteId() + "," + v.getFecha() + "," + v.getTotal() + "," + itemsToString(v.getItems()) + "\n");
            }
        }
    }

    @Override
    public void add(Venta v) throws IOException {
        List<Venta> all = readAll();
        int maxId = all.stream().mapToInt(Venta::getId).max().orElse(0);
        v.setId(maxId + 1);
        all.add(v);
        writeAll(all);
    }

    @Override
    public void update(Venta v) throws IOException {
        List<Venta> all = readAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == v.getId()) {
                all.set(i, v);
                found = true;
                break;
            }
        }
        if (!found) all.add(v);
        writeAll(all);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Venta> all = readAll();
        boolean removed = all.removeIf(x -> x.getId() == id);
        if (removed) writeAll(all);
    }

    @Override
    public Optional<Venta> findById(int id) throws IOException {
        return readAll().stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public List<Venta> findAll() throws IOException {
        return readAll();
    }
}
