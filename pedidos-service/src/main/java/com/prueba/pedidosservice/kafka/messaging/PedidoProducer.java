package com.prueba.pedidosservice.kafka.messaging;

import com.prueba.pedidosservice.kafka.event.PedidoCreado;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoProducer {
    private final KafkaTemplate<String, PedidoCreado> kafka;

    public void publicar(PedidoCreado evt) {
        // clave = pedidoId para mantener orden por pedido (particionado por key)
        kafka.send("pedidos.creados", evt.pedidoId().toString(), evt);
    }
}
