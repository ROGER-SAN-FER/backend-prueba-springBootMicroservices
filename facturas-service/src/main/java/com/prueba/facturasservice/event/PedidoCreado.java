// OJO: mismo paquete y nombre que en pedidos-service, hasta que tengas módulo contract
package com.prueba.facturasservice.event;

import java.math.BigDecimal;

public record PedidoCreado(Long pedidoId, Long productoId, BigDecimal total) {}
