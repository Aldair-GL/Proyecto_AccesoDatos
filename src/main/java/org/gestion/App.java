package org.gestion;

import org.gestion.dao.file.FileClienteDAO;
import org.gestion.dao.file.FileProductoDAO;
import org.gestion.dao.file.FileVentaDAO;
import org.gestion.model.Cliente;
import org.gestion.model.Producto;
import org.gestion.model.Venta;
import org.gestion.service.ClienteService;
import org.gestion.service.ProductoService;
import org.gestion.service.VentaService;

import java.io.IOException;
import java.util.*;

public class App {

    private static final String PROD_CSV = "src/main/resources/productos.csv";
    private static final String CLI_CSV  = "src/main/resources/clientes.csv";
    private static final String VEN_CSV  = "src/main/resources/ventas.csv";

    public static void main(String[] args) throws Exception {
        var productoDAO = new FileProductoDAO(PROD_CSV);
        var clienteDAO  = new FileClienteDAO(CLI_CSV);
        var ventaDAO    = new FileVentaDAO(VEN_CSV);

        var productoSrv = new ProductoService(productoDAO);
        var clienteSrv  = new ClienteService(clienteDAO);
        var ventaSrv    = new VentaService(productoDAO, clienteDAO, ventaDAO);

        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== SISTEMA DE GESTIÓN =====");
            System.out.println("1) Productos");
            System.out.println("2) Clientes");
            System.out.println("3) Ventas");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            int op = leerInt(sc);

            try {
                switch (op) {
                    case 1 -> menuProductos(sc, productoSrv);
                    case 2 -> menuClientes(sc, clienteSrv);
                    case 3 -> menuVentas(sc, productoSrv, clienteSrv, ventaSrv);
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida");
                }
            } catch (Exception ex) {
                System.out.println("⚠ Error: " + ex.getMessage());
            }
        }
        System.out.println("Programa finalizado.");
    }

    // ====== SUBMENÚS ======

    private static void menuProductos(Scanner sc, ProductoService srv) throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Productos ---");
            System.out.println("1) Listar");
            System.out.println("2) Añadir");
            System.out.println("3) Modificar");
            System.out.println("4) Eliminar");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            int op = leerInt(sc);

            switch (op) {
                case 1 -> srv.list().forEach(System.out::println);
                case 2 -> {
                    System.out.print("Nombre: "); String n = sc.nextLine();
                    System.out.print("Precio: "); double pr = leerDouble(sc);
                    System.out.print("Stock: "); int st = leerInt(sc);
                    var p = srv.add(n, pr, st);
                    System.out.println("Añadido: " + p);
                }
                case 3 -> {
                    System.out.print("ID del producto: "); int id = leerInt(sc);
                    var pOpt = srv.findById(id);
                    if (pOpt.isEmpty()) { System.out.println("No existe."); break; }
                    var p = pOpt.get();
                    System.out.println("Actual: " + p);
                    System.out.print("Nuevo nombre (enter para mantener): "); String n = sc.nextLine();
                    System.out.print("Nuevo precio (-1 para mantener): "); double pr = leerDouble(sc);
                    System.out.print("Nuevo stock (-1 para mantener): "); int st = leerInt(sc);

                    if (!n.isBlank()) p.setNombre(n);
                    if (pr >= 0) p.setPrecio(pr);
                    if (st >= 0) p.setStock(st);

                    srv.update(p);
                    System.out.println("Actualizado: " + p);
                }
                case 4 -> {
                    System.out.print("ID a eliminar: "); int id = leerInt(sc);
                    srv.delete(id);
                    System.out.println("Eliminado.");
                }
                case 0 -> back = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    private static void menuClientes(Scanner sc, ClienteService srv) throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Clientes ---");
            System.out.println("1) Listar");
            System.out.println("2) Añadir");
            System.out.println("3) Modificar");
            System.out.println("4) Eliminar");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            int op = leerInt(sc);

            switch (op) {
                case 1 -> srv.list().forEach(System.out::println);
                case 2 -> {
                    System.out.print("Nombre: "); String n = sc.nextLine();
                    System.out.print("Dirección: "); String d = sc.nextLine();
                    var c = srv.add(n, d);
                    System.out.println("Añadido: " + c);
                }
                case 3 -> {
                    System.out.print("ID del cliente: "); int id = leerInt(sc);
                    var cOpt = srv.findById(id);
                    if (cOpt.isEmpty()) { System.out.println("No existe."); break; }
                    var c = cOpt.get();
                    System.out.println("Actual: " + c);
                    System.out.print("Nuevo nombre (enter para mantener): "); String n = sc.nextLine();
                    System.out.print("Nueva dirección (enter para mantener): "); String d = sc.nextLine();

                    if (!n.isBlank()) c.setNombre(n);
                    if (!d.isBlank()) c.setDireccion(d);

                    srv.update(c);
                    System.out.println("Actualizado: " + c);
                }
                case 4 -> {
                    System.out.print("ID a eliminar: "); int id = leerInt(sc);
                    srv.delete(id);
                    System.out.println("Eliminado.");
                }
                case 0 -> back = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    private static void menuVentas(Scanner sc, ProductoService pSrv, ClienteService cSrv, VentaService vSrv) throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Ventas ---");
            System.out.println("1) Listar ventas");
            System.out.println("2) Registrar venta");
            System.out.println("3) Eliminar venta");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            int op = leerInt(sc);

            switch (op) {
                case 1 -> vSrv.listar().forEach(System.out::println);
                case 2 -> {
                    System.out.print("ID cliente: "); int clienteId = leerInt(sc);
                    Map<Integer, Integer> items = new LinkedHashMap<>();
                    boolean more = true;
                    while (more) {
                        System.out.print("ID producto: "); int pid = leerInt(sc);
                        System.out.print("Cantidad: "); int qty = leerInt(sc);
                        items.merge(pid, qty, Integer::sum);
                        System.out.print("¿Añadir otro producto? (s/n): ");
                        String r = sc.nextLine().trim();
                        more = r.equalsIgnoreCase("s");
                    }
                    Venta v = vSrv.registrarVenta(clienteId, items);
                    System.out.println("Venta registrada: " + v);
                }
                case 3 -> {
                    System.out.print("ID de venta a eliminar: "); int vid = leerInt(sc);
                    vSrv.eliminarVenta(vid);
                    System.out.println("Venta eliminada y stock restaurado.");
                }
                case 0 -> back = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    // ====== Utils de lectura segura ======

    private static int leerInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.print("Ingresa un entero válido: "); }
        }
    }

    private static double leerDouble(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.print("Ingresa un número válido: "); }
        }
    }
}
