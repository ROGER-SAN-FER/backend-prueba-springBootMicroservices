package com.prueba.pedidosservice.kafka.event;

import java.math.BigDecimal;

public record PedidoCreado(Long pedidoId, Long productoId, BigDecimal total) {}