package com.prueba.apigateway.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID);
        // ahora es efectivamente final
        final String cid = (header == null || header.isBlank()) ? UUID.randomUUID().toString() : header;

        // reconstruimos el request con el header (solo si no venía)
        ServerHttpRequest newRequest = (header == null || header.isBlank())
                ? exchange.getRequest().mutate().header(CORRELATION_ID, cid).build()
                : exchange.getRequest();

        // también lo agregamos/actualizamos en la respuesta
        exchange.getResponse().getHeaders().set(CORRELATION_ID, cid);

        // pasar el exchange con el request mutado
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; // alta prioridad
    }
}
