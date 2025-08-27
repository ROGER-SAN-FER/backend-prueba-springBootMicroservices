package com.prueba.pedidosservice.client;

import com.prueba.pedidosservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// client/ProductosClient.java
@FeignClient(
        name = "productos-service"                  // ✳️ Identificador del cliente con eureka, si no hay eureka se tiene que adicionar -> url = "${productos.url:http://localhost:8082}"
)
public interface ProductosClient {

    @GetMapping("/productos/{id}") // ✳️ Reutiliza semántica Spring MVC
    ProductDto getProductoById(@PathVariable("id") Long id);
}
