package org.gestion;

import org.gestion.dao.file.FileProductoDAO;
import org.gestion.dao.file.FileClienteDAO;
import org.gestion.dao.file.FileVentaDAO;
import org.gestion.model.Producto;
import org.gestion.model.Cliente;
import org.gestion.model.Venta;
import org.gestion.service.ProductoService;
import org.gestion.service.ClienteService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {
        FileProductoDAO productoDAO = new FileProductoDAO("src/main/resources/productos.csv");
        FileClienteDAO clienteDAO = new FileClienteDAO("src/main/resources/clientes.csv");
        FileVentaDAO ventaDAO = new FileVentaDAO("src/main/resources/ventas.csv");

        ProductoService productoService = new ProductoService(productoDAO);
        ClienteService clienteService = new ClienteService(clienteDAO);

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
            System.out.println("7. Editar Producto");
            System.out.println("8. Editar Cliente");
            System.out.println("9. Ajustar stock producto");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            try {
                switch (opcion) {
                    case 1 -> { // Añadir producto
                        System.out.print("Nombre del producto: ");
                        String nombre = sc.nextLine();
                        System.out.print("Precio: ");
                        double precio = sc.nextDouble();
                        System.out.print("Stock: ");
                        int stock = sc.nextInt();
                        sc.nextLine();
                        Producto p = productoService.addProducto(nombre, precio, stock);
                        System.out.println("Producto añadido con ID: " + p.getId());
                    }
                    case 2 -> { // Añadir cliente
                        System.out.print("Nombre del cliente: ");
                        String nombre = sc.nextLine();
                        System.out.print("Dirección: ");
                        String direccion = sc.nextLine();
                        Cliente c = clienteService.addCliente(nombre, direccion);
                        System.out.println("Cliente añadido con ID: " + c.getId());
                    }
                    case 3 -> { // Registrar venta (igual que antes)
                        System.out.print("ID del cliente: ");
                        int clienteId = sc.nextInt();
                        sc.nextLine();

                        if (clienteService.findById(clienteId).isEmpty()) {
                            System.out.println("Cliente no encontrado!");
                            break;
                        }

                        Map<Integer, Integer> items = new HashMap<>();
                        boolean añadirMas = true;

                        while (añadirMas) {
                            System.out.print("ID del producto: ");
                            int pid = sc.nextInt();
                            System.out.print("Cantidad: ");
                            int cantidad = sc.nextInt();
                            sc.nextLine();

                            Optional<Producto> prodOpt = productoService.findById(pid);
                            if (prodOpt.isEmpty()) {
                                System.out.println("Producto no encontrado!");
                                continue;
                            }

                            Producto prod = prodOpt.get();
                            if (prod.getStock() < cantidad) {
                                System.out.println("Stock insuficiente para " + prod.getNombre());
                                continue;
                            }

                            items.put(pid, cantidad);
                            System.out.print("¿Añadir otro producto? (s/n): ");
                            añadirMas = sc.nextLine().equalsIgnoreCase("s");
                        }

                        double total = 0;
                        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                            Producto prod = productoService.findById(entry.getKey()).get();
                            total += prod.getPrecio() * entry.getValue();
                            prod.setStock(prod.getStock() - entry.getValue());
                            productoService.updateProducto(prod);
                        }

                        String fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        Venta v = new Venta(0, clienteId, LocalDateTime.parse(fecha), items, total);
                        ventaDAO.add(v);
                        System.out.println("Venta registrada correctamente. Total: " + total);
                    }
                    case 4 -> { // listar productos
                        System.out.println("----- Productos -----");
                        productoService.listar().forEach(System.out::println);
                    }
                    case 5 -> { // listar clientes
                        System.out.println("----- Clientes -----");
                        clienteService.listar().forEach(System.out::println);
                    }
                    case 6 -> { // listar ventas
                        System.out.println("----- Ventas -----");
                        ventaDAO.findAll().forEach(System.out::println);
                    }
                    case 7 -> { // Editar producto
                        System.out.print("ID producto a editar: ");
                        int id = sc.nextInt(); sc.nextLine();
                        Optional<Producto> pOpt = productoService.findById(id);
                        if (pOpt.isEmpty()) { System.out.println("No existe"); break; }
                        Producto p = pOpt.get();
                        System.out.println("Actual: " + p);
                        System.out.print("Nuevo nombre (enter para mantener): ");
                        String name = sc.nextLine();
                        if (!name.isBlank()) p.setNombre(name);
                        System.out.print("Nuevo precio (o -1 para mantener): ");
                        double newPrice = sc.nextDouble();
                        if (newPrice >= 0) p.setPrecio(newPrice);
                        System.out.print("Nuevo stock (o -1 para mantener): ");
                        int newStock = sc.nextInt();
                        if (newStock >= 0) p.setStock(newStock);
                        sc.nextLine();
                        productoService.updateProducto(p);
                        System.out.println("Producto actualizado: " + p);
                    }
                    case 8 -> { // Editar cliente
                        System.out.print("ID cliente a editar: ");
                        int idc = sc.nextInt(); sc.nextLine();
                        Optional<Cliente> cOpt = clienteService.findById(idc);
                        if (cOpt.isEmpty()) { System.out.println("No existe"); break; }
                        Cliente c = cOpt.get();
                        System.out.println("Actual: " + c);
                        System.out.print("Nuevo nombre (enter para mantener): ");
                        String namec = sc.nextLine();
                        if (!namec.isBlank()) c.setNombre(namec);
                        System.out.print("Nueva dirección (enter para mantener): ");
                        String dir = sc.nextLine();
                        if (!dir.isBlank()) c.setDireccion(dir);
                        clienteService.updateCliente(c);
                        System.out.println("Cliente actualizado: " + c);
                    }
                    case 9 -> { // Ajustar stock (sumar o poner)
                        System.out.print("ID producto: ");
                        int pid = sc.nextInt(); sc.nextLine();
                        Optional<Producto> prodOpt = productoService.findById(pid);
                        if (prodOpt.isEmpty()) { System.out.println("No existe"); break; }
                        Producto prod = prodOpt.get();
                        System.out.println("Actual stock: " + prod.getStock());
                        System.out.print("Cantidad a añadir (puede ser negativa): ");
                        int qty = sc.nextInt(); sc.nextLine();
                        int nuevo = prod.getStock() + qty;
                        if (nuevo < 0) { System.out.println("No puede quedar stock negativo"); break; }
                        prod.setStock(nuevo);
                        productoService.updateProducto(prod);
                        System.out.println("Stock actualizado: " + prod.getStock());
                    }
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida");
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }

        sc.close();
        System.out.println("Programa finalizado.");
    }
}
