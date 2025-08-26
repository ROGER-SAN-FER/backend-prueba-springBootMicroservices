package com.prueba.authservice.features.oauth2.handler;

import com.prueba.authservice.features.audit.service.AuditService;
import com.prueba.authservice.features.auth.model.entity.RefreshTokenEntity;
import com.prueba.authservice.features.auth.repository.RefreshTokenRepository;
import com.prueba.authservice.features.auth.service.JwtService;
import com.prueba.authservice.features.identity.domain.UserEntity;
import com.prueba.authservice.features.oauth2.service.SocialProvisioningService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SocialProvisioningService provisioning;
    private final JwtService jwt;
    private final RefreshTokenRepository refreshRepo;
    private final AuditService audit;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException {
        var token = (OAuth2AuthenticationToken) authentication;
        var principal = token.getPrincipal();
        String provider = token.getAuthorizedClientRegistrationId(); // "google" | "facebook"

        // ---- 1) EXTRAER DATOS DEL PROVIDER ----
        String providerUserId;
        String email = null;
        String name  = null;

        if (principal instanceof OidcUser oidc) { // Google
            providerUserId = oidc.getSubject();
            email = oidc.getEmail();
            name  = oidc.getFullName();
        } else { // Facebook
            var attrs = ((OAuth2User) principal).getAttributes();
            providerUserId = String.valueOf(attrs.get("id"));
            email = (String) attrs.get("email");
            name  = (String) attrs.getOrDefault("name", email != null ? email : providerUserId);
        }

        // ---- 2) PROVISIONING JIT EN TU BD ----
        UserEntity user = provisioning.provisionOrUpdate(provider, providerUserId, email, name);

        // ---- 3) CONSTRUIR CLAIMS (ROLES + PERMISOS) ----
        var rolesYPermisos = new HashSet<String>();
        rolesYPermisos.addAll(
                user.getRolesList().stream()
                        .map(r -> "ROLE_" + r.getRoleEnum())
                        .collect(Collectors.toSet())
        );
        user.getRolesList().stream()
                .flatMap(r -> r.getAuthoritiesList().stream())
                .map(a -> a.getName())
                .forEach(rolesYPermisos::add);

        Map<String, Object> claims = new HashMap<>();
        claims.put("provider", provider);
        claims.put("uid", user.getId());
        claims.put("roles", new ArrayList<>(rolesYPermisos));
        if (email != null) claims.put("email", email);
        if (name  != null) claims.put("name",  name);

        // ---- 4) EMITIR TOKENS ----
        String username = user.getUsername();
        String access   = jwt.generateAccessToken(username, claims);
        String refresh  = jwt.generateRefreshToken(username);

        // ---- 5) PERSISTIR REFRESH (HASH + EXP) ----
        String tokenHash = sha256(refresh);
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(tokenHash)
                // usa el método que prefieras:
                //.expiresAt(jwt.refreshExpiresAtFromNow())
                .expiresAt(jwt.extractExpirationInstant(refresh))
                .build();
        refreshRepo.save(entity);

        // ---- 6) AUDITORÍA ----
        audit.record("LOGIN_OAUTH2_SUCCESS", username, "OK", "provider=" + provider);

        // ---- 7) RESPUESTA ----
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("""
            {"accessToken":"%s","refreshToken":"%s","tokenType":"Bearer"}
            """.formatted(access, refresh));
        res.getWriter().flush();
    }

    private String sha256(String token) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing refresh token", e);
        }
    }
}


//@Component
//@RequiredArgsConstructor
//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final SocialProvisioningService provisioning;
//    private final JwtService jwt;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//
//        var token = (OAuth2AuthenticationToken) authentication;
//        var principal = token.getPrincipal();
//        String provider = token.getAuthorizedClientRegistrationId(); // "google" | "facebook"
//
//        String providerUserId;
//        String email = null;
//        String name = null;
//
//        if (principal instanceof OidcUser oidc) {           // Google (OIDC)
//            providerUserId = oidc.getSubject();
//            email = oidc.getEmail();
//            name = oidc.getFullName();
//        } else {                                            // Facebook (OAuth2)
//            var attrs = ((OAuth2User) principal).getAttributes();
//            providerUserId = String.valueOf(attrs.get("id"));
//            email = (String) attrs.get("email");            // puede venir null si no se consintió
//            name = (String) attrs.getOrDefault("name", email != null ? email : providerUserId);
//        }
//
//        // 1) JIT provisioning en tu Postgres
//        UserEntity user = provisioning.provisionOrUpdate(provider, providerUserId, email, name);
//
//        // 2) Construir authorities desde TU BD (roles + permisos)
//        var authorities = new HashSet<String>();
//        // Roles como ROLE_X
//        authorities.addAll(
//                user.getRolesList().stream()
//                        .map(r -> "ROLE_" + r.getRoleEnum()) // ajusta si guardas el nombre de rol de otro modo
//                        .collect(Collectors.toSet())
//        );
//        // Permisos/authorities como nombres planos (p.ej. REPORT_READ)
//        user.getRolesList().stream()
//                .flatMap(r -> r.getAuthoritiesList().stream())
//                .map(a -> a.getName())
//                .forEach(authorities::add);
//
//        // 3) Claims para tu JWT
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("provider", provider);
//        claims.put("uid", user.getId());
//        claims.put("roles", new ArrayList<>(authorities));
//        if (email != null) claims.put("email", email);
//        if (name != null)  claims.put("name", name);
//
//        // 4) Emitir tus tokens
//        String access  = jwt.generateAccessToken(user.getUsername(), claims);
//        String refresh = jwt.generateRefreshToken(user.getUsername());
//
//        // 5) Responder JSON
//        response.setContentType("application/json");
//        response.getWriter().write("""
//            {"accessToken":"%s","refreshToken":"%s"}
//        """.formatted(access, refresh));
//    }
//}
