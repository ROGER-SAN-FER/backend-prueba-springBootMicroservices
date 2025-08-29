package com.prueba.productosservice.controller;

import com.prueba.productosservice.dto.ProductRequest;
import com.prueba.productosservice.dto.ProductResponse;
import com.prueba.productosservice.entity.ProductEntity;
import com.prueba.productosservice.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductoService productoService;
    @Value("${app.mensaje}")
    private String mensaje;

    @GetMapping("/mensaje")
    public String getMensaje() {
        return mensaje;
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productoService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable long id) {
        return toResponse(productoService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        ProductEntity toSave = new ProductEntity();
        toSave.setName(request.name());
        toSave.setPrice(request.price());
        return toResponse(productoService.create(toSave));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable long id, @Valid @RequestBody ProductRequest request) {
        // Traemos el existente para 404 si no existe y luego actualizamos campos
        ProductEntity existing = productoService.findById(id);
        existing.setName(request.name());
        existing.setPrice(request.price());
        return toResponse(productoService.create(existing)); // save() sirve para update también
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        ProductEntity existing = productoService.findById(id);
        productoService.delete(existing);
    }

    private ProductResponse toResponse(ProductEntity e) {
        return new ProductResponse(e.getId(), e.getName(), e.getPrice());
    }
}
