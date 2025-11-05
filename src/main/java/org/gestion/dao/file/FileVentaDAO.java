package org.gestion.dao.file;

import org.gestion.dao.VentaDAO;
import org.gestion.model.Venta;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileVentaDAO implements VentaDAO {
    private final File file;

    public FileVentaDAO(String filepath) {
        this.file = new File(filepath);
    }

    private List<Venta> readAll() throws IOException {
        List<Venta> list = new ArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("id,")) continue;

                // División respetando comillas
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 5) {
                    System.out.println("Línea ignorada (formato inválido): " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    int clienteId = Integer.parseInt(parts[1].trim());
                    String fecha = parts[2].trim();
                    double total = Double.parseDouble(parts[3].trim());
                    String items = parts[4].trim().replace("\"", "");

                    list.add(new Venta(id, clienteId, fecha, total, items));

                } catch (NumberFormatException e) {
                    System.out.println("Error de formato en línea: " + line);
                }
            }
        }
        return list;
    }

    private void writeAll(List<Venta> ventas) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            pw.println("id,clienteId,fecha,total,items");
            for (Venta v : ventas) {
                String itemsStr = v.getItems().entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(";"));
                String fechaStr = v.getFecha() != null ? v.getFecha().toString() : "";
                pw.printf("%d,%d,%s,%.2f,%s%n",
                        v.getId(),
                        v.getClienteId(),
                        fechaStr,
                        v.getTotal(),
                        itemsStr);
            }
        }
    }

    @Override
    public Venta add(Venta v) throws IOException {
        List<Venta> all = readAll();
        int maxId = all.stream().mapToInt(Venta::getId).max().orElse(0);
        v.setId(maxId + 1);
        all.add(v);
        writeAll(all);
        return v;
    }

    @Override
    public boolean delete(int id) throws IOException {
        List<Venta> all = readAll();
        boolean removed = all.removeIf(x -> x.getId() == id);
        if (removed) writeAll(all);
        return removed;
    }

    @Override
    public Venta update(Venta v) throws IOException {
        List<Venta> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == v.getId()) {
                all.set(i, v);
                writeAll(all);
                return v;
            }
        }
        throw new RuntimeException("Venta no encontrada");
    }

    @Override
    public Optional<Venta> findById(int id) throws IOException {
        return readAll().stream().filter(x -> x.getId() == id).findFirst();
    }

    @Override
    public List<Venta> findAll() throws IOException {
        return readAll();
    }
}
