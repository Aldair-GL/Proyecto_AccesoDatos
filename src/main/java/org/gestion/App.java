package org.gestion;

import org.gestion.dao.*;
import org.gestion.dao.file.*;
import org.gestion.dao.jdbc.*;
import org.gestion.model.*;
import org.gestion.service.*;

import java.io.IOException;
import java.util.*;

public class App {

    private static final String PROD_CSV = "src/main/resources/productos.csv";
    private static final String CLI_CSV  = "src/main/resources/clientes.csv";
    private static final String VEN_CSV  = "src/main/resources/ventas.csv";

    static FileProductoDAO fProdDAO = new FileProductoDAO(PROD_CSV);
    static FileClienteDAO fCliDAO  = new FileClienteDAO(CLI_CSV);
    static FileVentaDAO   fVenDAO  = new FileVentaDAO(VEN_CSV);

    static JdbcProductoDAO jProdDAO = new JdbcProductoDAO();
    static JdbcClienteDAO  jCliDAO  = new JdbcClienteDAO();
    static JdbcVentaDAO    jVenDAO  = new JdbcVentaDAO();

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== SELECCIONE MODO DE OPERACIÓN =====");
        System.out.println("1) Modo Ficheros CSV");
        System.out.println("2) Modo Base de Datos JDBC");
        System.out.println("3) Migrar datos desde CSV → BD");
        System.out.print("Opción: ");

        int modo = leerInt(sc);

        ProductoDAO productoDAO;
        ClienteDAO clienteDAO;
        VentaDAO ventaDAO;

        switch (modo) {

            case 1 -> {
                System.out.println("📁 Modo FICHEROS CSV activado.");

                productoDAO = new FileProductoDAO(PROD_CSV);
                clienteDAO  = new FileClienteDAO(CLI_CSV);
                ventaDAO    = new FileVentaDAO(VEN_CSV);
            }

            case 2 -> {
                System.out.println("🗄 Modo BASE DE DATOS JDBC activado.");

                productoDAO = new JdbcProductoDAO();
                clienteDAO  = new JdbcClienteDAO();
                ventaDAO    = new JdbcVentaDAO();
            }

            case 3 -> {
                migrarCSVaBD(fProdDAO, fCliDAO, fVenDAO, jProdDAO, jCliDAO, jVenDAO);
                return;
            }


            default -> {
                System.out.println("Opción no válida.");
                return;
            }
        }

        // Services universales (funcionan igual con CSV o BD)
        ProductoService productoSrv = new ProductoService(productoDAO);
        ClienteService clienteSrv   = new ClienteService(clienteDAO);
        VentaService ventaSrv       = new VentaService(productoDAO, clienteDAO, ventaDAO);

        ejecutarMenuPrincipal(sc, productoSrv, clienteSrv, ventaSrv);

        System.out.println("Programa finalizado ✅.");
    }

    // =============================================================
    // ====================== MENÚ PRINCIPAL ========================
    // =============================================================

    private static void ejecutarMenuPrincipal(Scanner sc,
                                              ProductoService productoSrv,
                                              ClienteService clienteSrv,
                                              VentaService ventaSrv) throws Exception {

        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== SISTEMA DE GESTIÓN =====");
            System.out.println("1) Productos");
            System.out.println("2) Clientes");
            System.out.println("3) Ventas");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            int op = leerInt(sc);

            switch (op) {
                case 1 -> menuProductos(sc, productoSrv);
                case 2 -> menuClientes(sc, clienteSrv);
                case 3 -> menuVentas(sc, productoSrv, clienteSrv, ventaSrv);
                case 0 -> salir = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    // =============================================================
    // =========================== MENÚS ============================
    // =============================================================

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
                    System.out.print("Precio: "); double p = leerDouble(sc);
                    System.out.print("Stock: "); int s = leerInt(sc);
                    var obj = srv.add(n, p, s);
                    System.out.println("✅ Añadido: " + obj);
                }

                case 3 -> {
                    System.out.print("ID: "); int id = leerInt(sc);
                    var p = srv.findById(id).orElse(null);
                    if (p == null) { System.out.println("No existe."); break; }

                    System.out.println("Actual: " + p);

                    System.out.print("Nuevo nombre (enter = igual): ");
                    String n = sc.nextLine();
                    if (!n.isBlank()) p.setNombre(n);

                    System.out.print("Nuevo precio (-1 = igual): ");
                    double pr = leerDouble(sc);
                    if (pr >= 0) p.setPrecio(pr);

                    System.out.print("Nuevo stock (-1 = igual): ");
                    int st = leerInt(sc);
                    if (st >= 0) p.setStock(st);

                    srv.update(p);
                    System.out.println("✅ Actualizado.");
                }

                case 4 -> {
                    System.out.print("ID a eliminar: "); int id = leerInt(sc);
                    srv.delete(id);
                    System.out.println("✅ Eliminado.");
                }

                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
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
                    System.out.println("✅ Añadido: " + c);
                }

                case 3 -> {
                    System.out.print("ID: "); int id = leerInt(sc);
                    var c = srv.findById(id).orElse(null);
                    if (c == null) { System.out.println("No existe."); break; }

                    System.out.print("Nuevo nombre (enter = igual): ");
                    String n = sc.nextLine();
                    if (!n.isBlank()) c.setNombre(n);

                    System.out.print("Nueva dirección (enter = igual): ");
                    String d = sc.nextLine();
                    if (!d.isBlank()) c.setDireccion(d);

                    srv.update(c);
                    System.out.println("✅ Actualizado.");
                }

                case 4 -> {
                    System.out.print("ID a eliminar: "); int id = leerInt(sc);
                    srv.delete(id);
                    System.out.println("✅ Eliminado.");
                }

                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void menuVentas(Scanner sc, ProductoService pSrv, ClienteService cSrv, VentaService vSrv) throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Ventas ---");
            System.out.println("1) Listar");
            System.out.println("2) Registrar venta");
            System.out.println("3) Eliminar venta");
            System.out.println("0) Volver");
            System.out.print("Opción: ");

            int op = leerInt(sc);

            switch (op) {
                case 1 -> vSrv.listar().forEach(System.out::println);

                case 2 -> {
                    System.out.print("ID cliente: "); int cid = leerInt(sc);

                    Map<Integer,Integer> items = new LinkedHashMap<>();
                    boolean more = true;

                    while (more) {
                        System.out.print("ID producto: "); int pid = leerInt(sc);
                        System.out.print("Cantidad: "); int qty = leerInt(sc);
                        items.merge(pid, qty, Integer::sum);

                        System.out.print("¿Añadir otro? (s/n): ");
                        more = sc.nextLine().equalsIgnoreCase("s");
                    }

                    var v = vSrv.registrarVenta(cid, items);
                    System.out.println("✅ Venta registrada: " + v);
                }

                case 3 -> {
                    System.out.print("ID venta: "); int id = leerInt(sc);
                    vSrv.eliminarVenta(id);
                    System.out.println("✅ Venta eliminada y stock restaurado.");
                }

                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // =============================================================
    // ======================= MIGRACIÓN BD =========================
    // =============================================================

    private static void limpiarTablasBD() throws IOException {
        try (var conn = org.gestion.db.ConnectionManager.getConnection();
             var st = conn.createStatement()) {

            st.execute("SET FOREIGN_KEY_CHECKS = 0");

            st.execute("TRUNCATE TABLE ventas");
            st.execute("TRUNCATE TABLE productos");
            st.execute("TRUNCATE TABLE clientes");

            st.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("🧹 BD limpiada correctamente.");
        } catch (Exception e) {
            throw new IOException("Error limpiando tablas BD", e);
        }
    }


    private static void migrarCSVaBD(FileProductoDAO fProdDAO, FileClienteDAO fCliDAO,
                                     FileVentaDAO fVenDAO,
                                     JdbcProductoDAO jProdDAO, JdbcClienteDAO jCliDAO, JdbcVentaDAO jVenDAO) throws IOException {

        System.out.println("🚀 Migrando datos CSV → BD...");

        // ✅ Primero limpiar BD
        limpiarTablasBD();

        // ✅ Insertar productos
        for (Producto p : fProdDAO.findAll()) {
            jProdDAO.add(p); // usa ID del CSV
        }

        // ✅ Insertar clientes
        for (Cliente c : fCliDAO.findAll()) {
            jCliDAO.add(c); // usa ID del CSV
        }

        // ✅ Insertar ventas
        for (Venta v : fVenDAO.findAll()) {
            jVenDAO.add(v); // ahora las FK existen
        }

        System.out.println("✅ Migración completada correctamente.");
    }


    // =============================================================
    // ========================= UTILIDADES =========================
    // =============================================================

    private static int leerInt(Scanner sc) {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Número inválido: "); }
        }
    }

    private static double leerDouble(Scanner sc) {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Número inválido: "); }
        }
    }
}
