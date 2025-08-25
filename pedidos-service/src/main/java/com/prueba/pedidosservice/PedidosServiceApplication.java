package com.prueba.pedidosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// ✳️ Activa el escaneo de interfaces @FeignClient - le dice a Spring que genere los proxies de Feign.
@EnableFeignClients (basePackages = "com.prueba.pedidosservice.client")
@SpringBootApplication
public class PedidosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosServiceApplication.class, args);
    }

}
