package org.gestion;

import org.gestion.dao.file.FileProductoDAO;
import org.gestion.dao.file.FileClienteDAO;
import org.gestion.dao.file.FileVentaDAO;
import org.gestion.model.Producto;
import org.gestion.model.Cliente;
import org.gestion.model.Venta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {

        // 1) Instanciar DAO con rutas de ficheros
        FileProductoDAO productoDAO = new FileProductoDAO("src/main/resources/productos.csv");
        FileClienteDAO clienteDAO = new FileClienteDAO("src/main/resources/clientes.csv");
        FileVentaDAO ventaDAO = new FileVentaDAO("src/main/resources/ventas.csv");

        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== SISTEMA DE GESTIÓN DE NEGOCIO =====");
            System.out.println("1. Añadir Producto");
            System.out.println("2. Añadir Cliente");
            System.out.println("3. Registrar Venta");
            System.out.println("4. Listar Productos");
            System.out.println("5. Listar Clientes");
            System.out.println("6. Listar Ventas");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.print("Nombre del producto: ");
                    String nombre = sc.nextLine();
                    System.out.print("Precio: ");
                    double precio = sc.nextDouble();
                    System.out.print("Stock: ");
                    int stock = sc.nextInt();
                    sc.nextLine();
                    Producto p = new Producto(0, nombre, precio, stock);
                    productoDAO.add(p);
                    System.out.println("Producto añadido con ID: " + p.getId());
                }
                case 2 -> {
                    System.out.print("Nombre del cliente: ");
                    String nombre = sc.nextLine();
                    System.out.print("Dirección: ");
                    String direccion = sc.nextLine();
                    Cliente c = new Cliente(0, nombre, direccion);
                    clienteDAO.add(c);
                    System.out.println("Cliente añadido con ID: " + c.getId());
                }
                case 3 -> {
                    System.out.print("ID del cliente: ");
                    int clienteId = sc.nextInt();
                    sc.nextLine();
                    Map<Integer, Integer> items = new HashMap<>();
                    boolean añadirMas = true;
                    while (añadirMas) {
                        System.out.print("ID del producto: ");
                        int pid = sc.nextInt();
                        System.out.print("Cantidad: ");
                        int cant = sc.nextInt();
                        items.put(pid, cant);
                        System.out.print("Añadir otro producto? (s/n): ");
                        sc.nextLine();
                        String resp = sc.nextLine();
                        añadirMas = resp.equalsIgnoreCase("s");
                    }

                    // Registrar venta
                    try {
                        // Validaciones manuales de stock y cliente
                        List<Producto> productos = productoDAO.findAll();
                        Optional<Cliente> clienteOpt = clienteDAO.findById(clienteId);
                        if (clienteOpt.isEmpty()) {
                            System.out.println("Cliente no encontrado!");
                            break;
                        }

                        // Restar stock y crear ventas
                        double total = 0;
                        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
                            int idProd = e.getKey();
                            int qty = e.getValue();
                            boolean stockOk = true;
                            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                                Producto prod = productoDAO.findById(entry.getKey()).orElse(null);
                                if (prod == null || prod.getStock() < entry.getValue()) {
                                    System.out.println("Stock insuficiente para " + (prod != null ? prod.getNombre() : "Producto ID " + entry.getKey()));
                                    stockOk = false;
                                }
                            }
                            if (!stockOk) break; // No registrar venta si hay algún producto sin stock

                            // Si todo ok, restar stock y guardar ventas
                            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                                Producto prod = productoDAO.findById(entry.getKey()).get();
                                prod.setStock(prod.getStock() - entry.getValue());
                                productoDAO.update(prod);
                                String fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                Venta v = new Venta(0, clienteId, entry.getKey(), entry.getValue(), entry.getValue() * prod.getPrecio(), fecha);
                                ventaDAO.add(v);
                                total += prod.getPrecio() * entry.getValue();
                            }

                        }

                        // Guardar venta por cada producto
                        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
                            Venta v = new Venta(0, clienteId, e.getKey(), e.getValue(), e.getValue() * productoDAO.findById(e.getKey()).get().getPrecio(), new Date().toString());
                            ventaDAO.add(v);
                        }
                        System.out.println("Venta registrada correctamente. Total: " + total);

                    } catch (Exception ex) {
                        System.out.println("Error registrando venta: " + ex.getMessage());
                    }
                }
                case 4 -> {
                    System.out.println("----- Productos -----");
                    for (Producto p : productoDAO.findAll()) System.out.println(p);
                }
                case 5 -> {
                    System.out.println("----- Clientes -----");
                    for (Cliente c : clienteDAO.findAll()) System.out.println(c);
                }
                case 6 -> {
                    System.out.println("----- Ventas -----");
                    for (Venta v : ventaDAO.findAll()) System.out.println(v);
                }
                case 0 -> salir = true;
                default -> System.out.println("Opción no válida");
            }
        }

        sc.close();
        System.out.println("Programa finalizado.");
    }
}