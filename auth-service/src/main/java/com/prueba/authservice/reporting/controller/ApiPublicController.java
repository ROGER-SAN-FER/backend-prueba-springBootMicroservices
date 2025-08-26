package com.prueba.authservice.reporting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiPublicController {
    @GetMapping("/public/ping")
    public String publicPing() {
        return "pong público (API)";
    }
}
