package com.prueba.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        // libre: flujo OAuth2 y endpoints públicos del auth-service
                        .pathMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers("/pedidos/**").authenticated()
                        .pathMatchers("/productos/**").authenticated()
                        // todo lo demás requiere JWT válido
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtGrantedAuthoritiesConverter()))
                );

        return http.build();
    }

    /**
     * Convierte el claim "roles" (string o array) en autoridades Spring:
     * ROLE_ADMIN, ROLE_USER, etc.
     */
    private ReactiveJwtAuthenticationConverterAdapter jwtGrantedAuthoritiesConverter() {
        var delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1) roles (ya vienen con ROLE_)
            var roles = toStringCollection(jwt.getClaim("roles")).stream()
                    .filter(r -> r.startsWith("ROLE_"))
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new);

            // 2) authorities (permisos planos)
            var auths = toStringCollection(jwt.getClaim("authorities")).stream()
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new);

            return java.util.stream.Stream.concat(roles, auths)
                    .collect(Collectors.toCollection(java.util.ArrayList::new));

        });
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }

    @SuppressWarnings("unchecked")
    private static java.util.Collection<String> toStringCollection(Object claim) {
        if (claim == null) return java.util.List.of();
        if (claim instanceof java.util.Collection<?> c) return c.stream().map(Object::toString).toList();
        return java.util.List.of(claim.toString());
    }
}

