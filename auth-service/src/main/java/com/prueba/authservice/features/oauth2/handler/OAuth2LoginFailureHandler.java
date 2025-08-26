package com.prueba.authservice.features.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.authservice.features.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final AuditService audit;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res,
                                        AuthenticationException ex) throws IOException {
        // Normalmente no hay principal disponible en fallo de OAuth2
        String principal = "anonymous";
        String provider = provider(req);

        // Mensaje/razón compacta
        String reason = reason(ex);

        // 1) Auditoría
        audit.record("LOGIN_OAUTH2_FAILURE", principal, "FAIL",
                "provider=" + provider + " reason=" + reason);

        // 2) Respuesta HTTP
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(om.writeValueAsString(Map.of(
                "error", "oauth2_authentication_failed",
                "provider", provider,
                "reason", reason
        )));
        res.getWriter().flush();
    }

    private String provider(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) return "unknown";
        return uri.contains("google") ? "google"
                : uri.contains("facebook") ? "facebook"
                : "unknown";
    }

    private String reason(AuthenticationException ex) {
        if (ex instanceof OAuth2AuthenticationException oae) {
            // Incluye el error code estándar de OAuth2 si existe
            return oae.getError() != null ? oae.getError().getErrorCode() : oae.getClass().getSimpleName();
        }
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) return ex.getClass().getSimpleName();
        // recorta por si es muy largo
        return msg.length() > 200 ? msg.substring(0, 200) : msg;
    }
}
