package org.gestion.dao.file;

import org.gestion.dao.VentaDAO;
import org.gestion.model.Venta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.*;

public class FileVentaDAO implements VentaDAO {
    private final File file;

    public FileVentaDAO(String filepath) { this.file = new File(filepath); }

    private List<Venta> readAll() throws IOException {
        List<Venta> list = new ArrayList<>();
        if(!file.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                if(line.startsWith("id,")) continue; // cabecera
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                int idCliente = Integer.parseInt(parts[1]);
                int idProducto = Integer.parseInt(parts[2]);
                int cantidad = Integer.parseInt(parts[3]);
                double total = Double.parseDouble(parts[4]);
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                Venta v = new Venta(id, idCliente, idProducto, cantidad, total, fecha);
                list.add(v);
            }
        }
        return list;
    }

    private void writeAll(List<Venta> ventas) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(file,false))) {
            pw.println("id,idCliente,idProducto,cantidad,total,fecha");
            for(Venta v : ventas) {
                pw.printf("%d,%d,%d,%d,%.2f,%s%n",
                        v.getId(),
                        v.getIdCliente(),
                        v.getIdProducto(),
                        v.getCantidad(),
                        v.getTotal(),
                        v.getFecha());
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
        if(removed) writeAll(all);
        return removed;
    }

    @Override
    public Venta update(Venta v) throws IOException {
        List<Venta> all = readAll();
        for(int i=0; i<all.size(); i++) {
            if(all.get(i).getId() == v.getId()) {
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
