package org.goblinmarket.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado a Long por estándar profesional

    @NotBlank(message = "El nombre del aventurero es obligatorio")
    private String nombre;

    private String direccion;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore // Evita que al ver un cliente se carguen todas sus ventas (pesado)
    private List<Venta> ventas;

    public Cliente() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public List<Venta> getVentas() { return ventas; }
    public void setVentas(List<Venta> ventas) { this.ventas = ventas; }
}