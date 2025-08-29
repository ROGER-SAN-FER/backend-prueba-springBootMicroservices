package com.prueba.pedidosservice.controller;

import com.prueba.pedidosservice.dto.PedidoView;
import com.prueba.pedidosservice.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidosController {

    private final PedidoService pedidoService;

    @GetMapping("/{pedidoId}/producto/{productoId}")
    public ResponseEntity<PedidoView> verPedidoConProducto(
            @PathVariable Long pedidoId,
            @PathVariable Long productoId) {
        // ✳️ Llama al servicio: Feign + Resilience4j se aplican “debajo”
        PedidoView view = pedidoService.consultarPedido(pedidoId, productoId);
        return ResponseEntity.ok(view);
    }
}