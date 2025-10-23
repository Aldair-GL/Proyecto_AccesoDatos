package org.gestion.dao.file;

import org.gestion.dao.ClienteDAO;
import org.gestion.model.Cliente;

import java.io.*;
import java.util.*;

public class FileClienteDAO implements ClienteDAO {
    private final File file;

    public FileClienteDAO(String filepath) { this.file = new File(filepath); }

    private List<Cliente> readAll() throws IOException {
        List<Cliente> list = new ArrayList<>();
        if(!file.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                if(line.startsWith("id,")) continue; // cabecera
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String nombre = parts[1];
                String direccion = parts[2];
                String historial = parts.length > 3 ? parts[3] : ""; // por si el campo está vacío
                list.add(new Cliente(id,nombre,direccion).setHistorialCompras(historial.replace(";", ",")));
            }
        }
        return list;
    }

    private void writeAll(List<Cliente> clientes) throws IOException {
        try(PrintWriter pw = new PrintWriter(new FileWriter(file,false))) {
            pw.println("id,nombre,direccion,historialCompras");
            for(Cliente c : clientes) {
                pw.printf("%d,%s,%s,%s%n",
                        c.getId(),
                        c.getNombre(),
                        c.getDireccion(),
                        c.getHistorialCompras().replace(",", ";")); // evitar comas rotas
            }
        }
    }

    @Override
    public Cliente add(Cliente c) throws IOException {
        List<Cliente> all = readAll();
        int maxId = all.stream().mapToInt(Cliente::getId).max().orElse(0);
        c.setId(maxId + 1);
        all.add(c);
        writeAll(all);
        return c;
    }

    @Override
    public boolean delete(int id) throws IOException {
        List<Cliente> all = readAll();
        boolean removed = all.removeIf(x -> x.getId() == id);
        if(removed) writeAll(all);
        return removed;
    }

    @Override
    public Cliente update(Cliente c) throws IOException {
        List<Cliente> all = readAll();
        for(int i=0; i<all.size(); i++) {
            if(all.get(i).getId() == c.getId()) {
                all.set(i, c);
                writeAll(all);
                return c;
            }
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    @Override
    public Optional<Cliente> findById(int id) throws IOException {
        return readAll().stream().filter(x -> x.getId() == id).findFirst();
    }

    @Override
    public List<Cliente> findAll() throws IOException {
        return readAll();
    }
}
