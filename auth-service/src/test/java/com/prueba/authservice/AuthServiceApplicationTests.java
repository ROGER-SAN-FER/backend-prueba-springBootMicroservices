package com.prueba.authservice;

import com.prueba.authservice.features.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:dummy.pem",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=disabled"
})
@ActiveProfiles("test")
class AuthServiceApplicationTests {

    @MockBean
    JwtService jwtService;

    @Test
    void contextLoads() {
    }

}
