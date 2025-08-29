package com.prueba.productosservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@EnableMethodSecurity // activa @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Podrías abrir health-checks
                        .requestMatchers("/actuator/**").permitAll()
                        // todo lo demás requiere JWT válido
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(this::jwtAuthConverter))
                );

        return http.build();
    }

    private AbstractAuthenticationToken jwtAuthConverter(Jwt jwt) {
        // Extrae roles del claim "roles" o "authorities"
        Collection<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            roles = jwt.getClaimAsStringList("authorities");
        }
        if (roles == null) roles = List.of();

        var authorities = roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}

