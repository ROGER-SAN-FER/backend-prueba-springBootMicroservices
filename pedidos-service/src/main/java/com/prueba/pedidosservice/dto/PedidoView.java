package com.prueba.pedidosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// dto/PedidoView.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoView {
    private Long pedidoId;
    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioProducto;
}
