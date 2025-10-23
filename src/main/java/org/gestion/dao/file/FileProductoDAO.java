package org.gestion.dao.file;

import org.gestion.dao.ProductoDAO;
import org.gestion.model.Producto;
import java.io.*;
import java.util.*;

public class FileProductoDAO implements ProductoDAO {
    private final File file;

    public FileProductoDAO(String filepath) { this.file = new File(filepath); }

    private List<Producto> readAll() throws IOException {
        List<Producto> list = new ArrayList<>();
        if(!file.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                if(line.startsWith("id,")) continue; // si tiene cabecera
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String nombre = parts[1];
                double precio = Double.parseDouble(parts[2]);
                int stock = Integer.parseInt(parts[3]);
                list.add(new Producto(id,nombre,precio,stock));
            }
        }
        return list;
    }

    private void writeAll(List<Producto> products) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(file,false))) {
            pw.println("id,nombre,precio,stock");
            for(Producto p: products) {
                pw.printf("%d,%s,%f,%d%n", p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
    }

    @Override
    public Producto add(Producto p) throws IOException {
        List<Producto> all = readAll();
        int maxId = all.stream().mapToInt(Producto::getId).max().orElse(0);
        p.setId(maxId + 1);
        all.add(p);
        writeAll(all);
        return p;
    }

    @Override
    public boolean delete(int id) throws IOException {
        List<Producto> all = readAll();
        boolean removed = all.removeIf(x -> x.getId() == id);
        if(removed) writeAll(all);
        return removed;
    }

    @Override
    public Producto update(Producto p) throws IOException {
        List<Producto> all = readAll();
        for(int i=0;i<all.size();i++){
            if(all.get(i).getId()==p.getId()){ all.set(i,p); writeAll(all); return p; }
        }
        throw new RuntimeException("Producto no encontrado");
    }

    @Override
    public Optional<Producto> findById(int id) throws IOException {
        return readAll().stream().filter(x -> x.getId()==id).findFirst();
    }

    @Override
    public List<Producto> findAll() throws IOException {
        return readAll();
    }
}
