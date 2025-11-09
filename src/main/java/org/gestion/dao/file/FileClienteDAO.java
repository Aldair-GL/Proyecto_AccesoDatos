package org.gestion.dao.file;

import org.gestion.dao.ClienteDAO;
import org.gestion.model.Cliente;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileClienteDAO implements ClienteDAO {
    private final Path path;

    public FileClienteDAO(String filepath) {
        this.path = Paths.get(filepath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "id,nombre,direccion,historialVentas\n", StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creando clientes.csv", e);
        }
    }

    private List<Cliente> readAll() throws IOException {
        List<Cliente> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id,")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String nombre = parts[1].trim();
                    String direccion = parts[2].trim();
                    String historial = parts.length > 3 ? parts[3].trim() : "";
                    Cliente c = new Cliente(id, nombre, direccion);
                    c.setHistorialFromCsv(historial);
                    list.add(c);
                } catch (NumberFormatException ignored) {
                    System.out.println("Fila cliente ignorada: " + line);
                }
            }
        }
        return list;
    }

    private void writeAll(List<Cliente> clientes) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,nombre,direccion,historialVentas\n");
            for (Cliente c : clientes) {
                String nombre = c.getNombre() == null ? "" : c.getNombre().replace(",", " ");
                String dir = c.getDireccion() == null ? "" : c.getDireccion().replace(",", " ");
                bw.write(c.getId() + "," + nombre + "," + dir + "," + c.getHistorialComprasCsv() + "\n");
            }
        }
    }

    @Override
    public void add(Cliente c) throws IOException {
        List<Cliente> all = readAll();
        int maxId = all.stream().mapToInt(Cliente::getId).max().orElse(0);
        c.setId(maxId + 1);
        all.add(c);
        writeAll(all);
    }

    @Override
    public void update(Cliente c) throws IOException {
        List<Cliente> all = readAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == c.getId()) {
                all.set(i, c);
                found = true;
                break;
            }
        }
        if (!found) all.add(c);
        writeAll(all);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Cliente> all = readAll();
        boolean removed = all.removeIf(cli -> cli.getId() == id);
        if (removed) writeAll(all);
    }

    @Override
    public Optional<Cliente> findById(int id) throws IOException {
        return readAll().stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public List<Cliente> findAll() throws IOException {
        return readAll();
    }
}
