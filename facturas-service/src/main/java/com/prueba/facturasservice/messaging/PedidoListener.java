package com.prueba.facturasservice.messaging;

import com.prueba.facturasservice.event.PedidoCreado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PedidoListener {

    @KafkaListener(topics = "pedidos.creados", groupId = "facturas")
    public void onPedidoCreado(
            PedidoCreado evt,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("FacturaService: recibido -> key={} partition={} offset={} payload={}",
                key, partition, offset, evt);

        // Aquí simulas "generar factura":
        // 1) Calcular/crear entidad Factura
        // 2) Guardar en BD
        // 3) (Opcional) publicar evento "facturas.emitidas"
    }
}