package org.goblinmarket.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {
    private Long id;
    private LocalDateTime fecha;
    private String nombreCliente;
    private Double total;
    private List<ItemDTO> items;


}