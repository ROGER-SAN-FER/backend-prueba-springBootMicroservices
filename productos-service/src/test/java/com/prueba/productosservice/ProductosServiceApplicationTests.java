package com.prueba.productosservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:dummy.pem",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=disabled"
})
class ProductosServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
