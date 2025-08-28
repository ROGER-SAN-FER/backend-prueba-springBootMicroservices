package com.prueba.productosservice.service;

import com.prueba.productosservice.entity.ProductEntity;
import com.prueba.productosservice.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService{

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductEntity findById(long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    @Override
    public ProductEntity create(ProductEntity productEntity) {
        // por si llega con id, nos aseguramos de que sea insert
        productEntity.setId(null);
        return productRepository.save(productEntity);
    }

    @Override
    public void delete(ProductEntity productEntity) {
        // si trae id, borramos por id; si no, borramos por entidad
        if (productEntity.getId() != null) {
            productRepository.deleteById(productEntity.getId());
        } else {
            productRepository.delete(productEntity);
        }
    }
}
