package org.gestion.dao.file;

import org.gestion.dao.ProductoDAO;
import org.gestion.model.Producto;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileProductoDAO implements ProductoDAO {
    private final Path path;

    public FileProductoDAO(String filepath) {
        this.path = Paths.get(filepath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "id,nombre,precio,stock\n", StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creando productos.csv", e);
        }
    }

    private List<Producto> readAll() throws IOException {
        List<Producto> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id,")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String nombre = parts[1].trim();
                    double precio = Double.parseDouble(parts[2].trim());
                    int stock = Integer.parseInt(parts[3].trim());
                    list.add(new Producto(id, nombre, precio, stock));
                } catch (NumberFormatException ignored) {
                    System.out.println("Fila producto ignorada: " + line);
                }
            }
        }
        return list;
    }

    private void writeAll(List<Producto> productos) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,nombre,precio,stock\n");
            for (Producto p : productos) {
                String nombre = p.getNombre() == null ? "" : p.getNombre().replace(",", " ");
                bw.write(p.getId() + "," + nombre + "," + p.getPrecio() + "," + p.getStock() + "\n");
            }
        }
    }

    @Override
    public void add(Producto p) throws IOException {
        List<Producto> all = readAll();
        int maxId = all.stream().mapToInt(Producto::getId).max().orElse(0);
        p.setId(maxId + 1);
        all.add(p);
        writeAll(all);
    }

    @Override
    public void update(Producto p) throws IOException {
        List<Producto> all = readAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == p.getId()) {
                all.set(i, p);
                found = true;
                break;
            }
        }
        if (!found) all.add(p);
        writeAll(all);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Producto> all = readAll();
        boolean removed = all.removeIf(prod -> prod.getId() == id);
        if (removed) writeAll(all);
    }

    @Override
    public Optional<Producto> findById(int id) throws IOException {
        return readAll().stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Producto> findAll() throws IOException {
        return readAll();
    }
}
