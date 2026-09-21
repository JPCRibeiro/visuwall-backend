package br.app.visuwall.auth.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshCookie {
    private static final String COOKIE_NAME = "refresh_token";
    private static final String COOKIE_PATH = "/api/auth";

    @Value("${app.cookie.secure:true}")
    private boolean secure;
    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;
    @Value("${jwt.refresh-token-ttl}")
    private Duration ttl;

    public ResponseCookie create(String value) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(COOKIE_PATH)
                .maxAge(ttl)
                .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}