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

    // Garantiza que el archivo exista y tenga encabezado
    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "id,nombre,precio,stock\n", StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Leer todos los productos del CSV
    private List<Producto> readAll() throws IOException {
        List<Producto> list = new ArrayList<>();
        if (Files.notExists(path)) return list;

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
                    System.out.println("Fila de producto ignorada por formato: " + line);
                }
            }
        }
        return list;
    }

    // Escribir toda la lista al CSV
    private void writeAll(List<Producto> productos) throws IOException {
        Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(tmp,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            pw.println("id,nombre,precio,stock");
            for (Producto p : productos) {
                String nombre = p.getNombre() != null ? p.getNombre().replace(",", " ") : "";
//                pw.printf("%d,%s,%.2f,%d%n", p.getId(), nombre, p.getPrecio(), p.getStock());
            }
        }
        Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    // === Implementaciones de la interfaz ===

    @Override
    public void add(Producto p) throws IOException {
        List<Producto> all = readAll();
        int maxId = all.stream().mapToInt(Producto::getId).max().orElse(0);
        p.setId(maxId + 1);
        all.add(p);
        writeAll(all);
    }

    @Override
    public List<Producto> findAll() throws IOException {
        return readAll();
    }

    @Override
    public Optional<Producto> findById(int id) throws IOException {
        return readAll().stream().filter(p -> p.getId() == id).findFirst();
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

        if (!found) {
            // si no existe, lo añadimos (permite crear producto nuevo desde update)
            int maxId = all.stream().mapToInt(Producto::getId).max().orElse(0);
            p.setId(maxId + 1);
            all.add(p);
        }

        writeAll(all);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Producto> all = readAll();
        boolean removed = all.removeIf(p -> p.getId() == id);
        if (removed) writeAll(all);
    }
}
