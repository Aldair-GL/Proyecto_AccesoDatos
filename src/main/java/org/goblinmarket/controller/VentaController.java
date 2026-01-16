package org.goblinmarket.controller;

import org.goblinmarket.model.Venta;
import org.goblinmarket.service.VentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarVentas();
    }

    @PostMapping
    public Venta realizarVenta(@RequestBody Venta venta) {
        return ventaService.registrarVenta(venta);
    }
}