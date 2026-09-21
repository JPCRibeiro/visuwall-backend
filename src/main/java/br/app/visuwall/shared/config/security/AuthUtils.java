package br.app.visuwall.shared.config.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class AuthUtils {
    private AuthUtils() {}

    public static UUID currentUserIdOrNull(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new IllegalStateException("Subject is null");
        }
        return UUID.fromString(subject);
    }

    public static UUID currentUserId(Authentication authentication) {
        UUID id = currentUserIdOrNull(authentication);
        if (id == null) throw new IllegalStateException("Usuário não autenticado");
        return id;
    }
}
