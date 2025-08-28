package com.prueba.productosservice.service;

import com.prueba.productosservice.entity.ProductEntity;

import java.util.List;

public interface ProductoService {
    ProductEntity findById(long id);
    List<ProductEntity> findAll();
    ProductEntity create(ProductEntity productEntity);
    void delete(ProductEntity productEntity);
}
