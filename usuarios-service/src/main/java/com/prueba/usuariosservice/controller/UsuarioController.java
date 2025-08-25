package com.prueba.usuariosservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Exponemos este controlador como REST
@RequestMapping("/usuarios") // Ruta base de este microservicio
public class UsuarioController {

    @GetMapping("/{id}") // Endpoint GET /usuarios/{id}
    public ResponseEntity<String> getUsuario(@PathVariable Long id) {
        return ResponseEntity.ok("Usuario con id " + id);
    }
}
