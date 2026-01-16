package org.goblinmarket.service;

import org.goblinmarket.model.*;
import org.goblinmarket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository, ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional // Si el stock falla en el 3er producto, se cancela toda la venta
    public Venta registrarVenta(Venta venta) {
        double totalVenta = 0;

        // Validar que el cliente existe antes de procesar
        if (venta.getCliente() == null || venta.getCliente().getId() == null) {
            throw new RuntimeException("Es necesario un cliente válido para la venta");
        }

        for (VentaItem item : venta.getItems()) {
            // Buscamos el producto en la DB para tener el precio y stock real
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Objeto místico no encontrado: ID " + item.getProducto().getId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("¡Por las barbas de un goblin! No hay suficiente stock de: " + producto.getNombre());
            }

            // Actualizamos stock del producto
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Configuramos el item de la venta
            item.setPrecioUnitario(producto.getPrecio()); // Congelamos el precio
            item.setVenta(venta); // Vinculamos el item con la venta actual

            totalVenta += item.getPrecioUnitario() * item.getCantidad();
        }

        venta.setTotal(totalVenta);
        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }
}