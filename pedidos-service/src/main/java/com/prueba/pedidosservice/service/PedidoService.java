package com.prueba.pedidosservice.service;

import com.prueba.pedidosservice.client.ProductosClient;
import com.prueba.pedidosservice.dto.PedidoView;
import com.prueba.pedidosservice.dto.ProductDto;
import com.prueba.pedidosservice.kafka.event.PedidoCreado;
import com.prueba.pedidosservice.kafka.messaging.PedidoProducer;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final ProductosClient productosClient;
    private final PedidoProducer producer;

    @CircuitBreaker(name = "productosClient", fallbackMethod = "cbFallback") // ✳️ Usa la config del yml
    @Retry(name = "productosClient")          // ✳️ Reintento controlado, usa también del yml
    @Bulkhead(name = "productosClient", type = Bulkhead.Type.SEMAPHORE) // ✳️ Limita concurrencia
    public PedidoView consultarPedido(Long pedidoId, Long productoId) {
        ProductDto producto = productosClient.getProductoById(productoId);

        // Publicamos el evento (en real, esto iría en el POST de crear pedido)
        producer.publicar(new PedidoCreado(pedidoId, productoId, producto.getPrice()));

        // ✳️ En real: cargarías el pedido de tu BD. Aquí simulamos:
        return new PedidoView(pedidoId, producto.getId(), producto.getName(), producto.getPrice());
    }

    // Se ejecuta cuando el circuito está ABIERTO (CallNotPermittedException),
    // o cualquier excepción que no gestione Feign por estar bloqueada la llamada.
    public PedidoView cbFallback(Long pedidoId, Long productoId, Throwable ex) {
        // Opcional: log específico del caso de CB abierto
        log.warn("Circuit Breaker abierto para productosClient: {}", ex.toString());
        return new PedidoView(pedidoId, productoId, "Producto no disponible", BigDecimal.ZERO);
    }
}
