package com.prueba.productosservice.controller;

import com.prueba.productosservice.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        // Simulación (luego usaremos BD)
        return ResponseEntity.ok(new ProductDto(id, "Teclado mecánico", new BigDecimal("59.90")));
    }
}

