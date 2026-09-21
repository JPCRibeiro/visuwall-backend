package br.app.visuwall.auth.controller;

import br.app.visuwall.auth.domain.RefreshCookie;
import br.app.visuwall.auth.domain.TokenPair;
import br.app.visuwall.auth.dto.request.LoginRequest;
import br.app.visuwall.auth.dto.request.RegisterRequest;
import br.app.visuwall.auth.dto.response.AccessTokenResponse;
import br.app.visuwall.auth.exception.InvalidRefreshTokenException;
import br.app.visuwall.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshCookie refreshCookie;

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        TokenPair pair = authService.register(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.create(pair.refreshToken()).toString())
                .body(AccessTokenResponse.from(pair));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest req) {
        TokenPair pair = authService.login(req);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.create(pair.refreshToken()).toString())
                .body(AccessTokenResponse.from(pair));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        TokenPair pair = authService.refresh(refreshToken);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.create(pair.refreshToken()).toString())
                .body(AccessTokenResponse.from(pair));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }
        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.clear().toString())
                .build();
    }
}
