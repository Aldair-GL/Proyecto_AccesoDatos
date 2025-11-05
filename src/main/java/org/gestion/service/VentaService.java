package org.gestion.service;

import org.gestion.dao.file.FileClienteDAO;
import org.gestion.dao.file.FileProductoDAO;
import org.gestion.dao.file.FileVentaDAO;
import org.gestion.model.Cliente;
import org.gestion.model.Producto;
import org.gestion.model.Venta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class VentaService {

    private final FileProductoDAO productoDao;
    private final FileClienteDAO clienteDao;
    private final FileVentaDAO ventaDao;

    private final Path productosPath;
    private final Path clientesPath;
    private final Path ventasPath;

    public VentaService(FileProductoDAO productoDao,
                        FileClienteDAO clienteDao,
                        FileVentaDAO ventaDao,
                        String productosFilePath,
                        String clientesFilePath,
                        String ventasFilePath) {
        this.productoDao = productoDao;
        this.clienteDao = clienteDao;
        this.ventaDao = ventaDao;
        this.productosPath = Paths.get(productosFilePath);
        this.clientesPath = Paths.get(clientesFilePath);
        this.ventasPath = Paths.get(ventasFilePath);
    }

    public Venta registerSale(int clienteId, Map<Integer, Integer> items) throws Exception {
        List<Producto> productos = productoDao.findAll();
        List<Cliente> clientes = clienteDao.findAll();
        List<Venta> ventas = ventaDao.findAll();

        Cliente cliente = clientes.stream().filter(c -> c.getId() == clienteId).findFirst()
                .orElseThrow();

        Map<Integer, Producto> productoMap = productos.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            int pid = e.getKey();
            int qty = e.getValue();
            Producto p = productoMap.get(pid);
            if (p == null) throw new RuntimeException("Producto no existe: " + pid);
            if (qty <= 0) throw new RuntimeException("Cantidad inválida para producto " + pid);
            if (p.getStock() < qty) throw new RuntimeException("Stock insuficiente para producto " + p.getNombre());
        }

        Path backupProd = backupFile(productosPath);
        Path backupCli = backupFile(clientesPath);
        Path backupVentas = backupFile(ventasPath);

        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            Producto p = productoMap.get(e.getKey());
            p.setStock(p.getStock() - e.getValue());
        }

        double total = 0.0;
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            Producto p = productoMap.get(e.getKey());
            total += p.getPrecio() * e.getValue();
        }
        int nuevoId = ventas.stream().mapToInt(Venta::getId).max().orElse(0) + 1;
        LocalDateTime fechaActual = LocalDateTime.now();

        Venta nueva = new Venta(nuevoId, clienteId, fechaActual, items, total);


        cliente.getHistorialComprasIds().add(nueva.getId());

        try {
            writeProductosAtomic(productosPath, productos);
            ventas.add(nueva);
            writeVentasAtomic(ventasPath, ventas);
            writeClientesAtomic(clientesPath, clientes);

            deleteIfExists(backupProd);
            deleteIfExists(backupCli);
            deleteIfExists(backupVentas);

            return nueva;

        } catch (Exception ex) {
            try {
                restoreBackup(backupProd, productosPath);
                restoreBackup(backupCli, clientesPath);
                restoreBackup(backupVentas, ventasPath);
            } catch (Exception re) {
                throw new RuntimeException("Error al escribir y también al restaurar backups: " + re.getMessage(), ex);
            }
            throw new RuntimeException("Error al registrar venta, se restauró estado anterior. Detalle: " + ex.getMessage(), ex);
        }
    }

    private Path backupFile(Path original) throws IOException {
        if (!Files.exists(original)) return null;
        Path bak = original.resolveSibling(original.getFileName().toString() + ".bak");
        Files.copy(original, bak, StandardCopyOption.REPLACE_EXISTING);
        return bak;
    }

    private void restoreBackup(Path backup, Path target) throws IOException {
        if (backup == null || !Files.exists(backup)) return;
        Files.copy(backup, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private void deleteIfExists(Path p) {
        if (p == null) return;
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }

    private void writeProductosAtomic(Path target, List<Producto> productos) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,nombre,precio,stock");
            bw.newLine();
            for (Producto p : productos) {
                bw.write(String.format("%d,%s,%.2f,%d", p.getId(), escapeCsv(p.getNombre()), p.getPrecio(), p.getStock()));
                bw.newLine();
            }
        }
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void writeVentasAtomic(Path target, List<Venta> ventas) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,clienteId,fecha,total,items");
            bw.newLine();
            for (Venta v : ventas) {
                String itemsStr = v.getItems().entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(";"));
                bw.write(String.format("%d,%d,%s,%.2f,%s", v.getId(), v.getClienteId(), v.getFecha().toString(), v.getTotal(), itemsStr));
                bw.newLine();
            }
        }
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void writeClientesAtomic(Path target, List<Cliente> clientes) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write("id,nombre,direccion,historialVentas");
            bw.newLine();
            for (Cliente c : clientes) {
                String hist = c.getHistorialComprasIds().stream().map(Object::toString).collect(Collectors.joining(";"));
                bw.write(String.format("%d,%s,%s,%s", c.getId(), escapeCsv(c.getNombre()), escapeCsv(c.getDireccion()), hist));
                bw.newLine();
            }
        }
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}